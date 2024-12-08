import sys
import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import cx_Oracle
import json

if len(sys.argv) < 2:
    print("Please provide a user_id (email) as an argument.")
    sys.exit(1)

user_id = sys.argv[1]

# Database connection parameters
dsn_tns = cx_Oracle.makedsn('localhost', 1521, service_name='xe')  # Adjust host, port, service_name as needed
connection = cx_Oracle.connect(user='c##radu', password='suntfrumos', dsn=dsn_tns)

# Step 1: Load Data From the Database
query = """
SELECT 
    o.user_email AS user_id, 
    oi.product_code AS product_id, 
    SUM(oi.quantity) AS quantity, 
    COUNT(*) AS frequency
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
GROUP BY o.user_email, oi.product_code
"""

data = pd.read_sql(query, con=connection)

connection.close()

# Step 2: Calculate Ratings
data['rating'] = ((data['QUANTITY'] - data['QUANTITY'].min()) / (data['QUANTITY'].max() - data['QUANTITY'].min())) + \
                 ((data['FREQUENCY'] - data['FREQUENCY'].min()) / (data['FREQUENCY'].max() - data['FREQUENCY'].min()))

# Create a User-Item Matrix
user_item_matrix = data.pivot_table(index='USER_ID', columns='PRODUCT_ID', values='rating', fill_value=0)

# Step 3: Calculate User Similarities
user_sim_matrix = cosine_similarity(user_item_matrix)
user_sim_df = pd.DataFrame(user_sim_matrix, index=user_item_matrix.index, columns=user_item_matrix.index)

# Step 4: Predict Ratings
def predict_ratings(user_id, product_id):
    user_ratings = user_item_matrix.loc[user_id]
    similar_users = user_sim_df[user_id].sort_values(ascending=False)[1:]  # Exclude self
    
    numerator = 0
    denominator = 0
    for other_user, sim_score in similar_users.items():
        if product_id in user_item_matrix.columns:
            numerator += sim_score * user_item_matrix.loc[other_user, product_id]
            denominator += abs(sim_score)
    
    return numerator / denominator if denominator > 0 else 0

# Step 5: Recommend Products
def recommend_products(user_id, top_n=5):
    predicted_ratings = {
        product: predict_ratings(user_id, product) 
        for product in user_item_matrix.columns if user_item_matrix.loc[user_id, product] == 0
    }
    recommended_products = sorted(predicted_ratings.items(), key=lambda x: x[1], reverse=True)[:top_n]
    return recommended_products

recommendations = recommend_products(user_id, top_n=5)

# Now we reconnect to the database to check promotions
connection = cx_Oracle.connect(user='c##radu', password='suntfrumos', dsn=dsn_tns)

# Extract just the product IDs from recommendations
recommended_product_ids = [prod_id for prod_id, rating in recommendations]

if recommended_product_ids:
    # Prepare a dynamic SQL with IN clause for product IDs
    # Using a list of placeholders
    placeholders = ', '.join([f":p{i}" for i in range(len(recommended_product_ids))])
    promo_query = f"SELECT product_id, discount FROM promotions WHERE product_id IN ({placeholders})"
    promo_params = {f"p{i}": recommended_product_ids[i] for i in range(len(recommended_product_ids))}
    cursor = connection.cursor()
    promo_data = cursor.execute(promo_query, promo_params).fetchall()

    # Convert promo_data to a dictionary {product_id: discount}
    promo_dict = {row[0]: row[1] for row in promo_data}
else:
    promo_dict = {}

connection.close()

# Add discount info to recommendations
# Current recommendations format: [(product_id, rating), ...]
# We'll output as a list of [product_id, rating, discount]
final_recommendations = []
for product_id, rating in recommendations:
    discount = promo_dict.get(product_id, 0)  # 0 if not on promotions
    final_recommendations.append([product_id, rating, discount])

print(json.dumps({"user": user_id, "recommendations": final_recommendations}))
