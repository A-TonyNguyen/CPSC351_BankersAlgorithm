// Filename: Bank.java
// Description: Header file for BankImplementation
//
// Author:  Tony Nguyen
// Date:    December 10, 2020

public interface Bank
{
    // Adds customer to the Bank
    public void addCustomer(int threadNum, int[] maxDemand, int[] allocated);

    // Updates the customer count 
    public void updateCustomerCount(int newCustomerCount);   
     
    // Used to get allocation, max, need, and available matrices
    public void getState(); 

    // Used to request resources, specify number of customer being added, and maxDemand for customer
    // This will return true if request is granted
    public boolean requestResources(int threadNum, int[] request);

    // Used to release resources
    public void releaseResources(int threadNum, int[] release);
}
