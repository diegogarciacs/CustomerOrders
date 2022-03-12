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

import java.time.LocalDateTime;
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
      // Create the list of owners in the database.
      customerOrders.createEntity (products);
      List <Customers> customers = new ArrayList<Customers>();
      List <Order_lines> orderlines = new ArrayList<Order_lines>();
      List <Orders> orders = new ArrayList<Orders>();
      customers.add((new Customers("Garcia","Diego","1296 Temple Ave.","90803","5627196643")));
      customers.add((new Customers("Armando","Bloom","4312 Cowboy Rd.","85924","5628195230")));
      customers.add((new Customers("Grando","Ralph","1234 Phillipains","74920","8194442234")));
      String com = "Y";


      while (Objects.equals(com, "Y")){
         System.out.println("What customer would you like to see? (Number from 1 - )");
//         @NamedNativeQuery(
//                 name  = "getAllEmployees",
//                 query = "SELECT firstName, lastName" +
//                         "FROM Customers",
//                 resultClass=Customers.class)
         System.out.println(customers);
         int customer = getInt();
         System.out.println(products);
         System.out.println("What product would you like to see?(1 - *)");
         int product = getInt();
         System.out.println("Please input information about your order.");
         System.out.println("Who's placing the order?");
         String identity = getString();
         LocalDateTime time = LocalDateTime.now();
         orders.add(new Orders(customers.get(customer - 1),time,"Diego"));
         Products p = products.get(product - 1);
         System.out.println("How many of the products would you like to order?");
         int numOrders = getInt();
         if (p.getUnits_in_stock() < numOrders){
            System.out.println("We only have this many in stock. " + p.getUnits_in_stock());
            numOrders = p.getUnits_in_stock();
         }
         double price = p.getUnit_list_price();
         double total = price * numOrders;
         if (com.equals("Y")){
            System.out.println("This is your total: " + total);

            System.out.println("Would you like to continue with this purchase? (Y/N)");
            com = getString();
         }
         if (com.equals("N")){
            break;
         }
         p.setUnits_in_stock(p.getUnits_in_stock() - numOrders);


         orderlines.add(new Order_lines(p,orders.get(orders.size()-1),numOrders));
         System.out.println("Would you like to continue? (Y/N)");
         com = getString();
      }
      customerOrders.createEntity (customers);
      customerOrders.createEntity (orders);
      customerOrders.createEntity (orderlines);


      // Commit the changes so that the new data persists and is visible to other users.
      tx.commit();
      LOGGER.fine("End of Transaction");

   } // End of the main method

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

   public static String getString() {
      Scanner in = new Scanner( System.in );
      String input = in.nextLine();
      return input;
   }
   public static void printList(List<Customers> c){
      for (int i = 0; i<1; i++){
         System.out.println(c);
      }
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
