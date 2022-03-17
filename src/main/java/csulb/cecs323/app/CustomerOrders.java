/*
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2018 Alvaro Monge <alvaro.monge@csulb.edu>
 *
 */

package csulb.cecs323.app;

// Import all of the entity classes that we have written for this application.
import csulb.cecs323.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simple application to demonstrate how to persist an object in JPA.
 * <p>
 * This is for demonstration and educational purposes only.
 * </p>
 * <p>
 *     Originally provided by Dr. Alvaro Monge of CSULB, and subsequently modified by Dave Brown.
 * </p>
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2021 David Brown <david.brown@csulb.edu>
 *
 */
public class CustomerOrders {
   /**
    * You will likely need the entityManager in a great many functions throughout your application.
    * Rather than make this a global variable, we will make it an instance variable within the CustomerOrders
    * class, and create an instance of CustomerOrders in the main.
    */
   private EntityManager entityManager;

   /**
    * The Logger can easily be configured to log to a file, rather than, or in addition to, the console.
    * We use it because it is easy to control how much or how little logging gets done without having to
    * go through the application and comment out/uncomment code and run the risk of introducing a bug.
    * Here also, we want to make sure that the one Logger instance is readily available throughout the
    * application, without resorting to creating a global variable.
    */
   private static final Logger LOGGER = Logger.getLogger(CustomerOrders.class.getName());

   /**
    * The constructor for the CustomerOrders class.  All that it does is stash the provided EntityManager
    * for use later in the application.
    * @param manager    The EntityManager that we will use.
    */
   public CustomerOrders(EntityManager manager) {
      this.entityManager = manager;
   }

   public static void main(String[] args) {
      LOGGER.fine("Creating EntityManagerFactory and EntityManager");
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("CustomerOrders");
      EntityManager manager = factory.createEntityManager();
      // Create an instance of CustomerOrders and store our new EntityManager as an instance variable.
      CustomerOrders customerOrders = new CustomerOrders(manager);
      // Any changes to the database need to be done within a transaction.
      // See: https://en.wikibooks.org/wiki/Java_Persistence/Transactions

      LOGGER.fine("Begin of Transaction");
      EntityTransaction tx = manager.getTransaction();


      tx.begin();
      // List of Products that I want to persist.  I could just as easily done this with the seed-data.sql
      List <Products> products = new ArrayList<Products>();
      // Load up my List with the Entities that I want to persist.  Note, this does not put them
      // into the database.
      products.add(new Products("076174517163", "16 oz. hickory hammer", "Stanely Tools", "1", 9.97, 50));
      products.add(new Products("012345678910", "4-Volt Max 1/4-in Cordless Screwdriver", "Craftsman", "2", 29.98, 15));
      products.add(new Products("052GBA892003", "4-Volt 1/4-in Cordless Screwdriver", "WORX", "3", 43.44, 20));
      products.add(new Products("BRU852024801", "Steel Head Fiberglass Sledge Hammer", "Kobalt", "4", 19.98, 42));
      customerOrders.createEntity (products);
      //create Customers arrayList
      List <Customers> customers = new ArrayList<Customers>();
      //create orderlines arrayList
      List <Order_lines> orderlines = new ArrayList<Order_lines>();
      //create orders arrayList
      List <Orders> orders = new ArrayList<Orders>();
      //populate customers
      customers.add((new Customers("Garcia","Diego","1296 Temple Ave.","90803","5627196643")));
      customers.add((new Customers("Armando","Bloom","4312 Cowboy Rd.","85924","5628195230")));
      customers.add((new Customers("Grando","Ralph","1234 Phillipains","74920","8194442234")));
      customerOrders.createEntity (customers);
      String com = "Y";
      while (com.equalsIgnoreCase("Y")){
         System.out.println("Who is the sales associate making the order? (Number from 1" +
                 " - "+ customers.size() +")");
         printCustomers(customers);
         int customer = getIntRange(1, customers.size());
         System.out.println("******Please input information about your order.******");
         System.out.println();
         System.out.println("Who's placing the order?");
         String identity = getString();
         System.out.println("Would you like to date this order yourself? (y/n)");
         String dateChoice = getString();
         LocalDateTime time = null;
         if (dateChoice.equalsIgnoreCase("Y")){
            System.out.println("Please input a valid date in format dd-MM-yyyy HH:mm:ss");
            String date = getString();
            time = dateInput(date);
         } else {
            time = LocalDateTime.now();
         }
         printProducts(products);
         System.out.println("What product would you like to see?(1 - "+products.size()+")");
         int productChoice = getIntRange(1, products.size());
         Products p = products.get(productChoice - 1);
         if (p.getUnits_in_stock() == 0){
            while (p.getUnits_in_stock() == 0){
               System.out.println("We don't have any more of " + p.getProd_name());
               System.out.println("Please choose something else.");
               products.remove(productChoice-1);
               printProducts(products);
               System.out.println("What product would you like to see?(1 - "+products.size()+")");

               productChoice = getIntRange(1,products.size());
               p = products.get(productChoice - 1);
            }
         }


         System.out.println("How many of the products would you like to order?");
         int numOrders = getInt();
         if (p.getUnits_in_stock() < numOrders && p.getUnits_in_stock() != 0){
            System.out.println("We only have this many in stock. " + p.getUnits_in_stock());
            numOrders = p.getUnits_in_stock();
         }
         double price = p.getUnit_list_price();
         double total = price * numOrders;
         if (com.equalsIgnoreCase("Y")){
            System.out.println("This would be your total: " + total);

            System.out.println("Would you like to continue with this purchase? (Y/N)");
            com = getString();
         }
         if (com.equalsIgnoreCase("N")){
            break;
         }
         orders.add(new Orders(customers.get(customer - 1),time,identity));
         p.setUnits_in_stock(p.getUnits_in_stock() - numOrders);
         p.setUnit_list_price(price);


         orderlines.add(new Order_lines(p,orders.get(orders.size()-1),numOrders));
         System.out.println("Would you like to continue? (Y/N)");
         com = getString();
      }

      customerOrders.createEntity (orders);
      customerOrders.createEntity (orderlines);


      // Commit the changes so that the new data persists and is visible to other users.
      tx.commit();
      LOGGER.fine("End of Transaction");

   } // End of the main method

   /**
    * Function that accepts a range of integers (inclusive) and returns a valid integer within that range.
    * @param low Inclusive min.
    * @param high Inclusive max
    * @return A valid integer from selected range from user.
    */
   public static int getIntRange( int low, int high ) {
      Scanner in = new Scanner( System.in );
      int input = 0;
      boolean valid = false;
      while( !valid ) {
         if( in.hasNextInt() ) {
            input = in.nextInt();
            if( input <= high && input >= low ) {
               valid = true;
            } else {
               System.out.println( "Invalid Range." );
            }
         } else {
            in.next(); //clear invalid string
            System.out.println( "Invalid Input." );
         }
      }
      return input;
   }

   /**
    * This prints all customers in the arraylist to display to the user.
    * @param c the arraylist object of customers.
    */
   public static void printCustomers(List <Customers> c){
      for (int i = 0; i < c.size(); i++){
         System.out.println(i+1 + ") "+c.get(i).getFirst_name());
      }
   }
   /**
    * This prints all products in the arraylist to display to the user.
    * @param p the arraylist object of products.
    */
   public static void printProducts(List <Products> p){
      for (int i = 0; i < p.size(); i++){
         System.out.println(i+1 + ") "+p.get(i).getProd_name());
      }
   }

   /**
    * This function checks date input and insures that a user cannot completely put wrong information or a date in
    * the future for an order.
    * @param userInput The string input of a user indicating date and time.
    * @return Returns a LocalDateTime object to be used in the function.
    */
   public static LocalDateTime dateInput(String userInput) {
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
      LocalDateTime date = null;
      LocalDateTime today = LocalDateTime.now();
      boolean isValid = false;
      while (!isValid){
         try {
            date = LocalDateTime.parse(userInput,dateFormat);
            if (date.isAfter(today)){
               Exception DateTimeParseException = null;
               throw DateTimeParseException;
            }
            isValid = true;

         } catch (Exception e){
            System.out.println("This is not valid.");
            System.out.println("Please input a valid date in format dd-MM-yyyy HH:mm:ss");
            userInput = getString();
         }
      }
      return date;
   }

   /**
    * Simple function to return a valid integer input from user.
    * @return valid integer input.
    */
   public static int getInt() {
      Scanner in = new Scanner(System.in);
      int input = 0;
      boolean valid = false;
      while (!valid) {
         if (in.hasNextInt()) {
            input = in.nextInt();
            valid = true;
         } else {
            in.next(); //clear invalid string
            System.out.println("Invalid Input.");
         }
      }
      return input;
   }

   /**
    * Function to get valid string input from user.
    * @return string input.
    */
   public static String getString() {
      Scanner in = new Scanner( System.in );
      String input = in.nextLine();
      return input;
   }

   /**
    * Create and persist a list of objects to the database.
    * @param entities   The list of entities to persist.  These can be any object that has been
    *                   properly annotated in JPA and marked as "persistable."  I specifically
    *                   used a Java generic so that I did not have to write this over and over.
    */
   public <E> void createEntity(List <E> entities) {
      for (E next : entities) {
         LOGGER.info("Persisting: " + next);
         // Use the CustomerOrders entityManager instance variable to get our EntityManager.
         this.entityManager.persist(next);
      }

      // The auto generated ID (if present) is not passed in to the constructor since JPA will
      // generate a value.  So the previous for loop will not show a value for the ID.  But
      // now that the Entity has been persisted, JPA has generated the ID and filled that in.
      for (E next : entities) {
         LOGGER.info("Persisted object after flush (non-null id): " + next);
      }
   } // End of createEntity member method

   /**
    * Think of this as a simple map from a String to an instance of Products that has the
    * same name, as the string that you pass in.  To create a new Cars instance, you need to pass
    * in an instance of Products to satisfy the foreign key constraint, not just a string
    * representing the name of the style.
    * @param UPC        The name of the product that you are looking for.
    * @return           The Products instance corresponding to that UPC.
    */
   public Products getProduct (String UPC) {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Products> products = this.entityManager.createNamedQuery("ReturnProduct",
              Products.class).setParameter(1, UPC).getResultList();
      if (products.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return products.get(0);
      }

   }// End of the getStyle method
} // End of CustomerOrders class
