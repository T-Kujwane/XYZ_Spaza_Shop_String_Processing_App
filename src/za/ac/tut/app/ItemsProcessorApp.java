/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package za.ac.tut.app;

import java.text.DecimalFormat;
import java.util.Arrays;
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
        String[] inventory = {"Simba_Salt&Vin&&50&&R17", "Switch_Energy&&60&&R10", "Topper_Mint&&43&&R12"};
        String[] newInventory = Arrays.copyOf(inventory, inventory.length);
        
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
                        //hasValidLoginCredentials = validateManagerCredentials(loginCredentials) || validateUserCredentials(loginCredentials);
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
                                        String[] splitItemData = itemString.split("&&");
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
        String[] splitCreds = loginCredentials.split("--");
        return splitCreds[0].equalsIgnoreCase("xyz_man@xyzspaza.co.za") && splitCreds[1].equals("man1@manager");
    }

    private static boolean validateUserCredentials(String loginCredentials) {
        String[] splitCreds = loginCredentials.split("--");
        String userName = splitCreds[0];
        String password = splitCreds[1];

        int custNo = Integer.parseInt(userName.split("@")[0].substring(userName.split("@")[0].length() - 2));

        return custNo >= 1 && custNo <= 10 && password.equals("I*amCustomer" + custNo);
    }

    private static String getNewItem() {
        System.out.print("Enter the new items to add in the format ite=\"itemName1\"&&qty=num&&pri=Rprice: ");
        return new Scanner(System.in).nextLine();
    }

    private static boolean validateItemFormat(String newItemString) {
        String[] splitItemData = newItemString.split("&&");

        return splitItemData.length == 3
                && splitItemData[0].substring(0, 4).equalsIgnoreCase("ite=")
                && splitItemData[1].substring(0, 4).equalsIgnoreCase("qty=")
                && splitItemData[2].substring(0, 4).equalsIgnoreCase("pri=");
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
        return itemString.split("&&")[0].replace("ite=", "");
    }

    private static int getItemQuantity(String itemString) {
        String itemQtyString = itemString.split("&&")[1];
        int itemQty = itemQtyString.contains("qty=") ? Integer.parseInt(itemQtyString.replace("qty=", "")) : Integer.parseInt(itemQtyString);
        return itemQty;
    }

    private static double getItemPrice(String itemString) {
        String[] splitItemData = itemString.split("&&");
        String itemPriceStr = splitItemData[2];

        double itemPrice = itemPriceStr.toLowerCase().contains("pri")
                ? Double.parseDouble(itemPriceStr.split("=")[1].toUpperCase().replace("R", ""))
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
            for (String item : inventory) {

                String itemName = getItemName(item);
                double itemPrice = getItemPrice(item);
                if (loggedInUser == 1) {
                    System.out.println(itemName + "\t" + getItemQuantity(item) + "\t\tR" + df.format(itemPrice));
                } else {
                    System.out.println(itemName + "\t" + "\tR" + df.format(itemPrice));
                }
            }

        } else {
            System.out.println("No items to display.");
        }
    }

    private static void displayHeaders(int loggedInUser) {
        if (loggedInUser == 1) {
            System.out.println("Item\t\tQuantity\tPrice");
        } else {
            System.out.println("Item\tPrice");
        }
    }
}
