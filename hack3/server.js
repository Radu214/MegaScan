const express = require('express');
const oracledb = require('oracledb');
const bcrypt = require('bcryptjs');

const app = express();
const port = 3000;

app.use(express.json());

const dbConfig = {
  user: "c##radu",
  password: "suntfrumos",
  connectString: "localhost:1521/xe"
};

app.get('/data', async (req, res) => {
  let connection;

  try {
    connection = await oracledb.getConnection(dbConfig);
    const result = await connection.execute(
      `SELECT * FROM produse`, 
      [],
      { outFormat: oracledb.OUT_FORMAT_OBJECT }
    );

    res.json(result.rows);
  } catch (err) {
    console.error(err);
    res.status(500).send('Error querying database');
  } finally {
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error(err);
      }
    }
  }
});

app.post('/register', async (req, res) => {
  const { email, password, firstName, lastName } = req.body;

  if (!email || !password || !firstName || !lastName) {
    return res.status(400).json({ error: 'All fields (email, password, firstName, lastName) are required' });
  }

  const hashedPassword = await bcrypt.hash(password, 10);

  let connection;
  try {
    connection = await oracledb.getConnection(dbConfig);
    const insertSql = `
      INSERT INTO users (email, password_hash, first_name, last_name)
      VALUES (:email, :password_hash, :first_name, :last_name)
    `;
    await connection.execute(
      insertSql,
      { email, password_hash: hashedPassword, first_name: firstName, last_name: lastName },
      { autoCommit: true }
    );

    res.json({ message: "User registered successfully." });
  } catch (err) {
    console.error(err);
    if (err.errorNum === 1) { 
      return res.status(400).json({ error: "Email already in use." });
    }
    res.status(500).json({ error: "Database error" });
  } finally {
    if (connection) {
      try { await connection.close(); } catch (err) { console.error(err); }
    }
  }
});

app.post('/login', async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  let connection;
  try {
    connection = await oracledb.getConnection(dbConfig);
    const query = `SELECT id, email, password_hash, first_name, last_name FROM users WHERE email = :email`;
    const result = await connection.execute(
      query,
      { email },
      { outFormat: oracledb.OUT_FORMAT_OBJECT }
    );

    if (result.rows.length === 0) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const user = result.rows[0];
    const passwordMatches = await bcrypt.compare(password, user.PASSWORD_HASH);

    if (passwordMatches) {
      res.json({ 
        message: "Login successful", 
        user: {
          id: user.ID, 
          email: user.EMAIL, 
          firstName: user.FIRST_NAME, 
          lastName: user.LAST_NAME 
        } 
      });
    } else {
      res.status(401).json({ error: "Invalid credentials" });
    }

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Database error" });
  } finally {
    if (connection) {
      try { await connection.close(); } catch (err) { console.error(err); }
    }
  }
});

app.get('/user/:email', async (req, res) => {
  const email = req.params.email;

  if (!email) {
    return res.status(400).json({ error: 'Email is required' });
  }

  let connection;
  try {
    connection = await oracledb.getConnection(dbConfig);

    const query = `
      SELECT first_name, last_name, email
      FROM users
      WHERE email = :email
    `;
    const result = await connection.execute(
      query,
      { email },
      { outFormat: oracledb.OUT_FORMAT_OBJECT }
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    const user = result.rows[0];
    res.json({
      firstName: user.FIRST_NAME,
      lastName: user.LAST_NAME,
      email: user.EMAIL,
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  } finally {
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error(err);
      }
    }
  }
});

app.post('/checkout', async (req, res) => {
  const { userEmail, items } = req.body;

  if (!userEmail || !items || !Array.isArray(items) || items.length === 0) {
    return res.status(400).json({ error: 'Invalid request: userEmail and items are required.' });
  }

  let connection;
  try {
    connection = await oracledb.getConnection(dbConfig);

    const insertOrderSql = `
      INSERT INTO orders (user_email, created_at)
      VALUES (:userEmail, SYSTIMESTAMP)
      RETURNING order_id INTO :order_id
    `;

    const orderResult = await connection.execute(
      insertOrderSql,
      {
        userEmail,
        order_id: { dir: oracledb.BIND_OUT, type: oracledb.NUMBER }
      },
      { autoCommit: false }
    );

    const orderId = orderResult.outBinds.order_id[0];

    const insertItemSql = `
      INSERT INTO order_items (order_id, product_code, quantity, price)
      VALUES (:order_id, :product_code, :quantity, :price)
    `;

    const binds = items.map(item => ({
      order_id: orderId,
      product_code: item.cod,
      quantity: item.quantity,
      price: item.pret
    }));

    await connection.executeMany(insertItemSql, binds, { autoCommit: true });

    res.json({ message: "Order placed successfully", orderId });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Database error: " + err.message });
  } finally {
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error(err);
      }
    }
  }
});

app.get('/orders/:email', async (req, res) => {
  const userEmail = req.params.email;

  if (!userEmail) {
    return res.status(400).json({ error: 'Email is required' });
  }

  let connection;
  try {
    connection = await oracledb.getConnection(dbConfig);

    const ordersQuery = `
      SELECT o.order_id, o.created_at
      FROM orders o
      WHERE o.user_email = :email
    `;
    const ordersResult = await connection.execute(
      ordersQuery,
      { email: userEmail },
      { outFormat: oracledb.OUT_FORMAT_OBJECT }
    );

    if (ordersResult.rows.length === 0) {
      return res.status(404).json({ message: 'No orders found for this user.' });
    }

    const orderIds = ordersResult.rows.map(order => order.ORDER_ID);

    const itemsQuery = `
      SELECT oi.order_id, p.denumire || ' '|| p.firma "PRODUCT_CODE" , oi.quantity, oi.price
      FROM order_items oi join produse p on (p.cod = oi.product_code)
      WHERE oi.order_id IN (${orderIds.join(",")})
    `;
    const itemsResult = await connection.execute(
      itemsQuery,
      {},
      { outFormat: oracledb.OUT_FORMAT_OBJECT }
    );

    const itemsGroupedByOrder = itemsResult.rows.reduce((acc, item) => {
      if (!acc[item.ORDER_ID]) {
        acc[item.ORDER_ID] = [];
      }
      acc[item.ORDER_ID].push({
        productCode: item.PRODUCT_CODE,
        quantity: item.QUANTITY,
        price: item.PRICE,
      });
      return acc;
    }, {});

    const orders = ordersResult.rows.map(order => ({
      orderId: order.ORDER_ID,
      createdAt: order.CREATED_AT,
      items: itemsGroupedByOrder[order.ORDER_ID] || [],
    }));

    res.json(orders);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Internal server error' });
  } finally {
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error(err);
      }
    }
  }
});

app.get('/recommendations/:email', async (req, res) => {
  const userEmail = req.params.email;

  if (!userEmail) {
    return res.status(400).json({ error: 'Email is required' });
  }

  const { spawn } = require('child_process');
  const pyProcess = spawn('python', ['recommend.py', userEmail]);

  let output = '';
  pyProcess.stdout.on('data', (data) => {
    output += data.toString();
  });

  pyProcess.stderr.on('data', (data) => {
    console.error(data.toString());
  });

  pyProcess.on('close', async (code) => {
    if (code !== 0) {
      return res.status(500).json({ error: 'Error running recommendation script' });
    }

    let connection;
    try {
      const recommendationsData = JSON.parse(output);
      const recommendations = recommendationsData.recommendations; 

      connection = await oracledb.getConnection(dbConfig);
      const updatedRecommendations = [];
      for (const [productId, rating, discount] of recommendations) {
        const query = `
          SELECT denumire, pret
          FROM produse
          WHERE cod = :cod
        `;
        const result = await connection.execute(query, { cod: productId }, { outFormat: oracledb.OUT_FORMAT_OBJECT });

        let denumire = 'Unknown Product';
        let pret = 0;

        if (result.rows.length > 0) {
          denumire = result.rows[0].DENUMIRE;
          pret = result.rows[0].PRET;
        }

        updatedRecommendations.push({
          productId,
          rating,
          name: denumire,
          price: pret,
          discount
        });
      }

      return res.json({
        user: recommendationsData.user,
        recommendations: updatedRecommendations
      });
    } catch (err) {
      console.error(err);
      return res.status(500).json({ error: 'Error processing recommendation results' });
    } finally {
      if (connection) {
        try {
          await connection.close();
        } catch (err) {
          console.error(err);
        }
      }
    }
  });
});

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});

bcrypt.hash('password123', 10, (err, hash) => {
    if (err) console.error(err);
    else console.log(hash);
});
