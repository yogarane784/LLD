interface MachineState {
    void selectProduct(VendingMachine machine, String slotId);
    void insertMoney(VendingMachine machine, int amount);
    void dispense(VendingMachine machine);
    void cancel(VendingMachine machine);
}
