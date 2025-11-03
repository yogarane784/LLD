## Decorator Design Pattern

- The Decorator Pattern allows you to add new behavior or responsibilities to an object dynamically — without modifying its existing code.
- It wraps objects inside decorator classes that implement the same interface.

💡 Key Idea
- Instead of subclassing to add features, you wrap an existing object with additional layers that extend its behavior.

#### In the Decorator pattern:
- You have a common interface (e.g., VehicleRental).
- You have a base implementation (e.g., CarRental).
- Each decorator also implements that same interface and holds a reference to another object of the same type (the “wrapped” object).

- When you create objects, you chain them together — each decorator adds behavior on top of the wrapped one.

#### Think of it like layers on an object.
```
VehicleRental rental = new GPSAddOn(
                            new InsuranceAddOn(
                                new HomePickupAddOn(
                                    new CarRental()
                                )
                            )
                        );
```

#### Each decorator:
- Has a reference to the “base” (rental inside its constructor),
- Delegates the core behavior (calls rental.getCost() and rental.getDescription()),
- Then adds its own behavior (adds cost or extra text).


#### Main.java
```
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        DataRepository repository = new DataRepository();
        RentalService<Vehicle> rentalService = new VehicleRentalServiceImpl(repository);

        rentalService = new HomePickupAddOnDecorator(rentalService);
        rentalService = new InsuranceAddOnDecorator(rentalService);
        rentalService = new GPSAddOnDecorator(rentalService);

        System.out.println(rentalService.getCost(new Inventory()));
    }
}
```

RentalService
```
public interface RentalService<V> {

    List<V> getResources(RentalResourceRequest rentalResourceRequest); // Get resources to render on UI for selection

    List<Inventory> getAvailability(InventoryRequest inventoryRequest); // upon resource selection, get the availability

    Double getCost(Inventory inventory); // get the cost for chosen slot from getAvailability
}
```


#### Base decorator : AddOnDecorator.java
```
/**
 * Decorator Base Class
 * All decorators will implement VehicleRental and hold a reference to another VehicleRental object.
 * @param <V>
 */
public class AddOnDecorator<V> implements RentalService<V> {

    RentalService<V> rentalService;

    public AddOnDecorator(RentalService<V> rentalService) {
        this.rentalService = rentalService;
    }

    @Override
    public List<V> getResources(RentalResourceRequest rentalResourceRequest) {
        return rentalService.getResources(rentalResourceRequest);
    }

    @Override
    public List<Inventory> getAvailability(InventoryRequest inventoryRequest) {
        return rentalService.getAvailability(inventoryRequest);
    }

    @Override
    public Double getCost(Inventory inventory) {
        return rentalService.getCost(inventory);
    }
}
```


#### HomePickupAddOnDecorator
```
public class HomePickupAddOnDecorator extends AddOnDecorator<Vehicle> {


    public HomePickupAddOnDecorator(RentalService<Vehicle> rentalService) {
        super(rentalService);
    }

    @Override
    public List<Vehicle> getResources(RentalResourceRequest rentalResourceRequest) {
        System.out.println();
        return rentalService.getResources(rentalResourceRequest);
    }

    @Override
    public List<Inventory> getAvailability(InventoryRequest inventoryRequest) {
        System.out.println();
        return rentalService.getAvailability(inventoryRequest);
    }

    @Override
    public Double getCost(Inventory inventory) {
        System.out.println();
        return rentalService.getCost(inventory) + 10;
    }
}
```


####
```
public class InsuranceAddOnDecorator extends AddOnDecorator<Vehicle> {

    public InsuranceAddOnDecorator(RentalService<Vehicle> rentalService) {
        super(rentalService);
    }

    @Override
    public List<Vehicle> getResources(RentalResourceRequest rentalResourceRequest) {
        System.out.println();
        return rentalService.getResources(rentalResourceRequest);
    }

    @Override
    public List<Inventory> getAvailability(InventoryRequest inventoryRequest) {
        System.out.println();
        return rentalService.getAvailability(inventoryRequest);
    }

    @Override
    public Double getCost(Inventory inventory) {
        System.out.println();
        return rentalService.getCost(inventory) + 100;
    }
}

```


#### GPSAddOnDecorator.java
```
public class GPSAddOnDecorator extends AddOnDecorator<Vehicle>{

    public GPSAddOnDecorator(RentalService<Vehicle> rentalService) {
        super(rentalService);
    }

    @Override
    public List<Vehicle> getResources(RentalResourceRequest rentalResourceRequest) {
        System.out.println();
        return rentalService.getResources(rentalResourceRequest);
    }

    @Override
    public List<Inventory> getAvailability(InventoryRequest inventoryRequest) {
        System.out.println();
        return rentalService.getAvailability(inventoryRequest);
    }

    @Override
    public Double getCost(Inventory inventory) {
        System.out.println();
        return rentalService.getCost(inventory) + 1000;
    }
}
```



- Notice how we are adding to the cost in every decorator
- Output of the code : 1110.0
