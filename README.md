# MegaScan

**MegaScanPay** is a mobile app that enhances the in-store shopping experience at **Mega Image**. With this app, users can scan products directly with their phone camera, add them to a digital cart, and pay seamlessly—skipping the checkout lines. The app also provides personalized product recommendations based on available offers using a cosine similarity algorithm.

> This project was built in just **24 hours** during the **HackITall** hackathon, for the AD/01 sponsor.

---

## Features

- **Scan Products with Your Camera**  
  Use your phone camera to scan barcodes and instantly add products to your cart.

- **User Accounts**  
  Create and manage personal accounts for a customized experience.

- **Digital Shopping Cart**  
  Keep track of scanned items in a real-time virtual cart.

- **Payment Flow (Prototype)**  
  In the context of the hackathon, we simulated a checkout screen to demonstrate how users would pay directly from their phones.

- **Smart Recommendations**  
  Receive personalized offers based on your shopping history and current store promotions.

- **Age-Restricted Product Control**  
  Items marked 18+ require in-person employee confirmation before being added to the cart.

- **AI Assistant**  
  Ask about cooking recipes, ingredient substitutions, product information, and more with the built-in AI-powered assistant.

---

## How Recommendations Work

MegaScanPay uses a cosine similarity algorithm to suggest relevant offers by analyzing shopping behavior and current promotions.

- The system identifies customers with shopping patterns similar to yours.
- It compares your scanned products with their purchase history using cosine similarity.
- Products frequently bought by similar users, but not yet in your cart, are given a relevance score.
- If those products are also part of current store promotions, they are recommended to you as personalized deals.

---


## Tech Stack

- **Frontend:** Java, XML  
- **Backend:** Java   
- **Recommendations logic:** Python  
- **AI Assistant:** OpenAI API
