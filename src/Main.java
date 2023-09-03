import java.io.*;
import java.util.*;

public class Main {
    static String[] files = {   "./Code/src/Test_Cases/input_01.txt",
                                "./Code/src/Test_Cases/input_02.txt",
                                "./Code/src/Test_Cases/input_03.txt",
                                "./Code/src/Test_Cases/input_04.txt",
                                "./Code/src/Test_Cases/input_05.txt",
                                "./Code/src/Test_Cases/input_06.txt"};
    static HashMap<String, Item> inventory = new HashMap<>();
    static ArrayList<String> itemsInInventory = new ArrayList<>();
    static ArrayList<SalesRecord> salesRecord = new ArrayList<>();
    static ArrayList<Record> record = new ArrayList<>();
    static Double profit = 0.00;
    static String profitString = "";

    public static void main(String[] args) throws Exception{
        for(int i=0; i<files.length; i++) {
            System.out.println("\ninput_0" + (i+1) + ".txt");
            System.out.println("~~~~~~~~~~~~~");
            ReadFile(i);
            ClearAll();
        }
    }
    static void ClearAll(){
    /* Clear every variable back to default */
        profit = 0.00;
        profitString = "";
        inventory.clear();
        itemsInInventory.clear();
        salesRecord.clear();
        record.clear();
    }

    static void ReadFile(Integer fileIndex) {
    /* read the .txt file*/
        String lineString = "";

        try {
            FileReader file = new FileReader(files[fileIndex]);
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                lineString = fileScanner.nextLine();
                ReadFileContent(lineString);
            }

        } catch (FileNotFoundException e){
            // file not found
            System.out.println("An error occurred, file not found!");
        }
    }
    static void ReadFileContent(String fileContent) {
    /* read the contents of the file and determine what function to run based on the content */
        int index = 0;
        boolean fileCheck = false;
        String [] fileContentArray = fileContent.split(" ");
        // add
        if (fileContentArray[0].equals("ADD")){
            index = 4;
            //check that there is a value in all the array
            fileCheck = CheckStringArray(fileContentArray, index);
            if (fileCheck){
                Add(fileContentArray[1],Integer.parseInt(fileContentArray[2]),Double.parseDouble(fileContentArray[3]));
            } else {
                ErrorWrongNumberOfArguments("Add",fileContentArray);
            }
        //sell
        } else if (fileContentArray[0].equals("SELL")){
            index = 4;
            //check that there is a value in all the array
            fileCheck = CheckStringArray(fileContentArray, index);
            if (fileCheck){
                Sell(fileContentArray[1],Integer.parseInt(fileContentArray[2]),Double.parseDouble(fileContentArray[3]));
            } else {
                ErrorWrongNumberOfArguments("Sell",fileContentArray);
            }
        // check
        } else if (fileContentArray[0].equals("CHECK")){
            index = 1;
            //check that there is a value in all the array
            fileCheck = CheckStringArray(fileContentArray, index);
            if (fileCheck){
                Check();
            } else {
                ErrorWrongNumberOfArguments("Check",fileContentArray);
            }
        // profit
        }else if (fileContentArray[0].equals("PROFIT")){
            index = 1;
            //check that there is a value in all the array
            fileCheck = CheckStringArray(fileContentArray, index);
            if (fileCheck){
                Profit();
            } else {
                ErrorWrongNumberOfArguments("Profit",fileContentArray);
            }
        // donate
        } else if (fileContentArray[0].equals("DONATE")){
            index = 3;
            //check that there is a value in all the array
            fileCheck = CheckStringArray(fileContentArray, index);
            if (fileCheck){
                Donate(fileContentArray[1], Integer.parseInt(fileContentArray[2]));
            } else {
                ErrorWrongNumberOfArguments("Donate",fileContentArray);
            }
        // write off
        } else if (fileContentArray[0].equals("WRITEOFF")){
            index = 3;
            //check that there is a value in all the array
            fileCheck = CheckStringArray(fileContentArray, index);
            if (fileCheck){
                WriteOff(fileContentArray[1], Integer.parseInt(fileContentArray[2]));
            } else {
                ErrorWrongNumberOfArguments("WriteOff",fileContentArray);
            }
        // return
        } else if (fileContentArray[0].equals("RETURN")){
            index = 4;
            //check that there is a value in all the array
            fileCheck = CheckStringArray(fileContentArray, index);
            if (fileCheck){
                Return(fileContentArray[1], Integer.parseInt(fileContentArray[2]), Double.parseDouble(fileContentArray[3]));
            } else {
                ErrorWrongNumberOfArguments("Return",fileContentArray);
            }
        }

    }
    static boolean CheckStringArray(String[] fileContentArray, Integer index){
    /* check to see if there is the correct amount of values for a given function and returns a boolean */
        if (index != fileContentArray.length) {
            return false;
        } else {
            return true;
        }
    }
    static void Add(String itemName, Integer qty, Double price){
    /* Add - Uses the input arguments to Add item into a hashmap */
        if(inventory.containsKey(itemName)){
            record.add(new Record(itemName, qty, price));
            Item existingItem = inventory.get(itemName);
            existingItem.qty += qty; //add to existing qty
        } else {
            itemsInInventory.add(itemName);
            record.add(new Record(itemName, qty, price));
            inventory.put(itemName, new Item(itemName, qty, price));  //add a new record to inventory
        }
    }

    static void Sell(String itemName, Integer qty, Double price) {
    /* Sell - Uses the input arguments to sell the items from the hashmap */
        if(inventory.containsKey(itemName)){
            salesRecord.add(new SalesRecord(itemName,price));

            record.add(new Record(itemName, qty, price));
            Item sellItem = inventory.get(itemName);
            if(qty > sellItem.qty){
                profitString = "NA";
            }else {
                for (Record item : record) {
                    if (item.itemName.equals(itemName)) {
                        //use previous sales and qty
                        Double prevPrice = item.price;
                        int prevQty = item.qty;
                        if (qty <= prevQty) {
                            profit += qty * (price - prevPrice);
                            sellItem.qty -= qty;
                            break;
                        } else {
                            profit += prevQty * (price - prevPrice);
                            qty -= prevQty;
                            sellItem.qty -= prevQty;
                        }
                    }
                }
            }
        }
    }
    static void WriteOff(String itemName, Integer qty){
    /* Remove the qty from the item at a cost price loss*/
        if(inventory.containsKey(itemName)){
            Item writeoffItem = inventory.get(itemName);
            profit -= qty * writeoffItem.price;
            writeoffItem.qty -= qty;
        }else {
            System.out.println("Error " + itemName + " doesn't exist cannot be written off!" );
        }
    }
    static void Donate(String itemName, Integer qty){
    /* Remove item from inventory as a donation doesn't impact profit */
        if(inventory.containsKey(itemName)){
            Item donateItem = inventory.get(itemName);
            donateItem.qty -= qty;
        }
    }

    static void Return(String itemName, Integer qty, Double price) {
        /* return a qty of an item at a specific price */
        if (inventory.containsKey(itemName)) {
            Item returnItem = inventory.get(itemName);
            boolean priceFound = false;

            //check that the price has been used before
            for (SalesRecord item : salesRecord) {

                if (item.itemName.equals(itemName) && item.price.equals(price)) {
                    priceFound = true;
                    break;
                }
            }

            if (priceFound) {
                if (returnItem.qty > qty) {
                    profit -= qty * (price - returnItem.price);
                }
            } else {
                profitString = "NA";
            }
        } else {
            System.out.println("Error: " + itemName + " doesn't exist and cannot be returned!");
        }
    }
    static void Profit(){
    /* Print out the total profit/loss*/
        if (profitString.equals("NA")){
            System.out.println("Profit/Loss: " + profitString);
        } else {
            System.out.println("Profit/Loss: " + String.format("%.2f", profit)); // round total to 2 decimal places
        }
    }

    static void ErrorWrongNumberOfArguments(String function, String[] fileContentArray) {
    /* function for printing out the wrong number of arguments error */
        System.out.println("---------ERROR---------");
        System.out.println("There was an error in the text file.");
        System.out.println("There is an incorrect amount of arguments for the function:  " + fileContentArray[0]);
        switch (function) {
            case "Add" -> System.out.println("Correct way: ADD <itemname> <qty> <price>");
            case "Sell" -> System.out.println("Correct way: SELL <itemname> <qty> <price>");
            case "Check" -> System.out.println("Correct way: CHECK");
            case "Profit" -> System.out.println("Correct way: PROFIT");
            case "Donate" -> System.out.println("Correct way: DONATE <itemname> <qty>");
            case "WriteOff" -> System.out.println("Correct way: WRITEOFF <itemname> <qty>");
            case "Return" -> System.out.println("Correct way: RETURN <itemname> <qty> <price>");
        }
        System.out.println("-----------------------");
    }
    static void Check(){
    /* print out all items and their qty */
        //find each item within the inventory and print qty
        for(int i = 1; i <= itemsInInventory.size(); i++){
            Item checkInventory = inventory.get(itemsInInventory.get(itemsInInventory.size()-i));
            System.out.println(checkInventory.itemName + ": " + checkInventory.qty);
        }
    }
}