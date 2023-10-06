/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package za.ac.tut.app;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author Thato Keith Kujwane
 */
public class ItemsProcessorApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String[] defaultInventory = {"Simba_Salt&Vin&&50&&R17", "Switch_Energy&&60&&R10", "Topper_Mint&&43&&R12"};
        String[] newInventory = Arrays.copyOf(defaultInventory, defaultInventory.length);

        Scanner scanner = new Scanner(System.in);

        boolean managerWantsToClose = false;

        while (!managerWantsToClose) {

            int loggedInUser = getLoggedInUser(scanner);

            boolean isValidUserType = validateUserType(loggedInUser);

            if (!isValidUserType) {
                System.err.println("Invalid user type option. Try again.\n");
            } else {
                String loginCredentials;
                boolean hasValidLoginCredentials;

                if (loggedInUser == 1) {

                    do {
                        loginCredentials = getLoginCredentials(scanner);
                        hasValidLoginCredentials = validateManagerCredentials(loginCredentials);
                        if (!hasValidLoginCredentials) {
                            System.err.println("Invalid login credentials. Try again.");
                        }
                    } while (!hasValidLoginCredentials);
                    boolean managerIsLogeedIn = true;

                    while (managerIsLogeedIn && !managerWantsToClose) {
                        displayItems(loggedInUser, newInventory);
                        int managerMenuOption;
                        boolean isValidManagerOption;

                        do {
                            managerMenuOption = getManagerMenuOption(scanner);
                            isValidManagerOption = validateManagerMenuOption(managerMenuOption);

                            if (!isValidManagerOption) {
                                System.err.println("Invalid menu option entered. Try again.\n");
                            }
                        } while (!isValidManagerOption);

                        managerWantsToClose = managerMenuOption == 3;

                        if (!managerWantsToClose) {
                            if (managerMenuOption == 1) {
                                String itemString;
                                boolean itemHasValidFormat, qtyIsNumeric = true, priceIsNumeric = true;

                                do {
                                    itemString = getNewItem();
                                    itemHasValidFormat = validateItemFormat(itemString);

                                    if (!itemHasValidFormat) {
                                        System.err.println("Invalid format used. Try again.\n");
                                    } else {
                                        String[] splitItemData = split(itemString, "&&");
                                        String qtyString = splitItemData[1].split("=")[1];

                                        qtyIsNumeric = validateNumericEntry(qtyString);

                                        if (!qtyIsNumeric) {
                                            System.err.println("Invalid numeric format for quantity (qty).\n");
                                        } else {
                                            String priceString = splitItemData[2].toUpperCase().split("=")[1].replace("R", "");
                                            priceIsNumeric = validateNumericEntry(priceString);

                                            if (!priceIsNumeric) {
                                                System.err.println("Invalid numeric entry for price.");
                                            }
                                        }
                                    }
                                } while (!(itemHasValidFormat && qtyIsNumeric && priceIsNumeric));

                                String itemName = getItemName(itemString);

                                int itemIndex = getItemIndex(newInventory, itemName);

                                if (itemIndex == -1) {
                                    newInventory = Arrays.copyOf(newInventory, newInventory.length + 1);
                                    int lastItemIndex = newInventory.length - 1;

                                    int itemQty = getItemQuantity(itemString);
                                    double itemPrice = getItemPrice(itemString);

                                    newInventory[lastItemIndex] = itemName + "&&" + itemQty + "&&R" + itemPrice;
                                    System.out.println("New item successfully added.");
                                } else {
                                    String item = newInventory[itemIndex];
                                    int itemQty = getItemQuantity(item);

                                    int newItemQty = getItemQuantity(itemString) + itemQty;

                                    newInventory[itemIndex] = item.replace(String.valueOf(itemQty), String.valueOf(newItemQty));
                                    System.out.println("Existing item quantity incremented.");
                                }
                            } else {
                                managerIsLogeedIn = false;
                            }
                        }
                    }
                } else {
                    do {
                        loginCredentials = getLoginCredentials(scanner);
                        hasValidLoginCredentials = validateCustomerCredentials(loginCredentials);
                        if (!hasValidLoginCredentials) {
                            System.err.println("Invalid login credentials. Try again.");
                        }
                    } while (!hasValidLoginCredentials);

                    boolean userIsLogginOut = false;

                    while (!userIsLogginOut) {
                        
                        displayItems(loggedInUser, newInventory);
                        int userItem;
                        boolean userMadeValidSelection;
                        
                        do {
                            userItem = getUserItem(scanner);
                            userMadeValidSelection = validateUserItemChoice(userItem, newInventory);
                            userIsLogginOut = userItem == -1;

                            if (!userMadeValidSelection) {
                                System.err.println("Invalid item option entered. Try again.");
                            }
                        } while (!userMadeValidSelection);
                        
                        if (!userIsLogginOut) {
                            
                            int quantity;
                            boolean hasEnoughSupply, isAvailable, isValidQty;
                            String purchasedItemName = getItemName(newInventory[--userItem]);
                            
                            do {
                                quantity = getDesiredQuantity(purchasedItemName, scanner);
                                isAvailable = determineIfAvailable(userItem, newInventory);
                                
                                isValidQty = quantity > 0;
                                
                                if (!isAvailable) {
                                    System.err.println(purchasedItemName + " is out of stock.");
                                    break;
                                } else if (isValidQty) {
                                    hasEnoughSupply = determineIfHasSufficientSupply(quantity, newInventory, userItem);

                                    if (!hasEnoughSupply) {
                                        System.err.println("Requested quantity is more than available. Please reduce.");
                                    }
                                }else {
                                    System.err.println("Invalid quantity requested. Quantity cannot be less than 1.");
                                    hasEnoughSupply = true;
                                }
                            } while (!hasEnoughSupply || !isValidQty);

                            if (isAvailable) {
                                decrementQuantity(quantity, userItem, newInventory);
                                double amountDue = getItemPrice(newInventory[userItem]) * quantity;
                                double payment;

                                boolean isSufficientPayment;

                                do {
                                    System.out.print("Enter payment for " + quantity + " " + purchasedItemName + " due at R" + amountDue + ": ");
                                    payment = scanner.nextDouble();

                                    isSufficientPayment = payment >= amountDue;

                                    if (!isSufficientPayment) {
                                        System.err.println("The amount payed is insufficient ot cover the R" + amountDue + " amount due.");
                                    }
                                } while (!isSufficientPayment);

                                double change = payment - amountDue;
                                
                                displayReceipt(purchasedItemName, quantity, amountDue, payment, change);
                            }
                        }
                    }
                }
            }
        }
    }

    private static int getLoggedInUser(Scanner sc) {
        System.out.print("Choose the type of user to login below\n"
                + "------------------------------------------------\n"
                + "1 - Manager\n"
                + "2 - Customer\n"
                + "Enter the number that corresponds to your type here: ");
        return sc.nextInt();
    }

    private static boolean validateManagerMenuOption(int menuOption) {
        return menuOption == 1 || menuOption == 2 || menuOption == 3;
    }

    private static boolean validateUserType(int userType) {
        return userType == 1 || userType == 2;
    }

    private static int getManagerMenuOption(Scanner sc) {
        System.out.print("Choose an operation to perform below\n"
                + "---------------------------------------------------\n"
                + "1 - Add new items\n"
                + "2 - Log out\n"
                + "3 - Stop application\n"
                + "Enter option here: ");
        return sc.nextInt();
    }

    private static String getLoginCredentials(Scanner sc) {
        System.out.print("Enter login credentials: ");
        return sc.next();
    }

    private static boolean validateManagerCredentials(String loginCredentials) {
        String[] splitCreds = split(loginCredentials, "--");
        return splitCreds[0].equalsIgnoreCase("xyz_man@xyzspaza.co.za") && splitCreds[1].equals("man1@manager");
    }

    private static boolean validateCustomerCredentials(String loginCredentials) {
        String[] splitCreds = split(loginCredentials, "--");
        String userName = splitCreds[0];
        String password = splitCreds[1];

        //cust2@xyzspaza.co.za  || cust02@xyzspaza.co.za
        String custNoStr = split(userName, "@")[0].toLowerCase().replace("cust", "");

        int custNo = Integer.parseInt(custNoStr);

        return custNo >= 1 && custNo <= 10 && password.equals("I*amCustomer" + custNoStr);
    }

    private static String getNewItem() {
        System.out.print("Enter the new items to add in the format ite=\"itemName1\"&&qty=num&&pri=Rprice: ");
        return new Scanner(System.in).nextLine();
    }

    private static String[] split(String splitString, String delimeter) {
        return splitString.split(delimeter);
    }

    private static boolean validateItemFormat(String newItemString) {
        String[] splitItemData = split(newItemString, "&&");

        return splitItemData.length == 3
                && split(splitItemData[0], "=")[0].equalsIgnoreCase("ite")
                && split(splitItemData[1], "=")[0].equalsIgnoreCase("qty")
                && split(splitItemData[2], "=")[0].equalsIgnoreCase("pri");
    }

    private static boolean validateNumericEntry(String numericString) {
        for (char stringChar : numericString.toCharArray()) {
            if (!Character.isDigit(stringChar)) {
                return false;
            }
        }
        return true;
    }

    private static String getItemName(String itemString) {
        return split(itemString, "&&")[0].replace("ite=", "").replace("\"", "");
    }

    private static int getItemQuantity(String itemString) {
        String itemQtyString = split(itemString, "&&")[1];
        int itemQty = itemQtyString.contains("qty=") ? Integer.parseInt(itemQtyString.replace("qty=", "")) : Integer.parseInt(itemQtyString);
        return itemQty;
    }

    private static double getItemPrice(String itemString) {
        String[] splitItemData = split(itemString, "&&");
        String itemPriceStr = splitItemData[2];

        double itemPrice = itemPriceStr.toLowerCase().contains("pri")
                ? Double.parseDouble(split(itemPriceStr, "=")[1].toUpperCase().replace("R", ""))
                : Double.parseDouble(itemPriceStr.replace("R", ""));

        return itemPrice;
    }

    private static int getItemIndex(String[] inventory, String itemName) {
        for (int i = 0; i < inventory.length; i++) {
            String item = inventory[i];

            if (item.toLowerCase().contains(itemName.toLowerCase())) {
                return i;
            }
        }

        return -1;
    }

    private static void displayItems(int loggedInUser, String[] inventory) {
        if (inventory.length != 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            displayHeaders(loggedInUser);

            for (int i = 0; i < inventory.length; ++i) {
                String item = inventory[i];
                String itemName = getItemName(item);
                double itemPrice = getItemPrice(item);
                if (loggedInUser == 1) {
                    System.out.println(itemName + "\t" + getItemQuantity(item) + "\t\tR" + df.format(itemPrice));
                } else {
                    System.out.println((i + 1) + ". " + itemName + "\t" + "\tR" + df.format(itemPrice));
                }
            }
        } else {
            System.out.println("No items to display.");
        }
    }

    private static void displayHeaders(int loggedInUser) {
        if (loggedInUser == 1) {
            System.out.println("\nItem\t\tQuantity\tPrice");
        } else {
            System.out.println("\nItem\t\t\tPrice");
        }
    }

    private static int getUserItem(Scanner sc) {
        System.out.print("Enter the number corresponding with your desired item or -1 to sign-out: ");
        int userItem = sc.nextInt();
        return userItem;
    }

    private static boolean validateUserItemChoice(int userChoice, String[] inventory) {
        return (userChoice >= -1 && userChoice <= inventory.length);
    }

    private static int getDesiredQuantity(String item, Scanner scanner) {
        System.out.print("Enter the desired number of " + item + " to purchase: ");
        return scanner.nextInt();
    }

    private static boolean determineIfAvailable(int itemIndex, String[] inventory) {
        int itemQuantity = getItemQuantity(inventory[itemIndex]);
        return itemQuantity > 0;
    }

    private static boolean determineIfHasSufficientSupply(int desiredQuantity, String[] inventory, int itemIndex) {
        int availQuantity = getItemQuantity(inventory[itemIndex]);

        return availQuantity >= desiredQuantity;
    }

    private static void decrementQuantity(int purchasedQuantity, int purchasedItem, String[] inventory) {
        String item = inventory[purchasedItem];
        int initQty = getItemQuantity(item);

        int updatedQuantity = initQty - purchasedQuantity;

        inventory[purchasedItem] = item.replace(String.valueOf(initQty), String.valueOf(updatedQuantity));
    }

    public static void displayReceipt(String purchasedItem, int quantity, double amountDue, double amountPaid, double change) {
        DecimalFormat df = new DecimalFormat("0.00");
        //cust2@xyzspaza.co.za--I*amCustomer2
        //xyz_man@xyzspaza.co.za--man1@manager
        System.out.println("-----------------------------------------------------\n"
                + "\t\tTransaction successfull\n"
                + "-----------------------------------------------------\n"
                + "The transaction performed above was successful. See transaction details below\n"
                + "Purchased item: " + purchasedItem + "\n"
                + "Quantity: " + quantity + "\n"
                + "Amount due: R" + df.format(amountDue) + "\n"
                + "Cash tendered: R" + df.format(amountPaid) + "\n"
                + "Change issued: R" + df.format(change) + "\n"
                + "Transaction completion date: " + new Date() + "\n" //Just messing around here
                + "-----------------------------------------------------\n");
    }
}
