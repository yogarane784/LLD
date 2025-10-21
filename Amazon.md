Functional Requirements 
- Users can browse different categories to find products
- Users can search for products/categories etc
- Users can add to cart
- Support for different payment types eg. Card, UPI, NetBanking etc
- Users can place order
- Users can add reviews
- Sellers can add/update products and update their inventory.
- Notifications on purchase/order updates
- Guest users can browse/search/add to cart, but to buy, they need to login/register

Non functional Requirements :
- Scale : Scale for big shopping events like Big Billion Day/ Prime Day
- Consistency on inventory : Should handle concurrent requests eg. if only one quantity is available for a product, it must bot be alloted to more than one customer
- Latency : Low latency for browse and search
- Real time price updates

Core Entities : 
- Product
- Category
- Order
- Cart
- Review
- Inventory
- Payment
- User

```
class Product {
  private long id;
  private String name;
  private Strong description;
  private Category category
  private long createdAt;
  private ProductStatus status;
}
```

```
class ProductVariations {
  private long id;
  private String name;
  private Strong description;
  private Map<> color;
  private String 
}
``` 

```
String Category {
  private long id;
  private String name;
  private long createdAt;
  private Category subCategories[];
}
```

