import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

//Used interface for better modularity
interface BankingOperations{
    void check_bal();
    void deposit(double amt);

    void transfer(Bankaccnt recipient, double amount);

    void withdraw(double amt);

}

class Bankaccnt implements BankingOperations{
  private String acntname;
  private String acntnum;
  private double balance;
  private int pin;

  //constructor
    public Bankaccnt(String acntname,String acntnum, double balance,int pin){
        this.acntname=acntname;
        this.acntnum=acntnum;
        this.balance=balance;
        this.pin=pin;
    }

    @Override
    public synchronized void check_bal() {
        System.out.println("Account Holder:"+acntname);
        System.out.println("Account number:"+acntnum);
        System.out.println("Available Balance:"+balance);
    }

    @Override
    public synchronized void deposit(double amt) {
        balance+=amt;
        System.out.print("Deposited amount "+amt+"/n New balance:"+balance);
    }
    @Override
    public synchronized void transfer(Bankaccnt recipient, double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            recipient.deposit(amount);
            System.out.println("Transfer successful. New Balance: $" + balance);
        } else {
            System.out.println("Transfer failed. Invalid amount.");
        }
    }

    @Override
    public synchronized void withdraw(double amt) {
        if(amt>0 &&amt<=balance){
            balance-=amt;
            System.out.println("Withdrawl aucessful. New balance:$"+balance);
        } else if (amt>balance) {
            System.out.println("Insufficient balance. Try checking balance again");
        }
    }

    public String getAcntnum(){
        return acntnum;
    }

    public int getPin(){
        return pin;
    }

    public String getActName() {
    return acntname;
    }
    public String toString(){
        return acntname+","+acntnum+","+balance+","+pin;
    }
    public static Bankaccnt fromString(String data){
        String[] parts=data.split(",");
        return new Bankaccnt(parts[0],parts[1],Double.parseDouble(parts[2]),Integer.parseInt(parts[3]));
    }
}
//Main class
public class Main {
    private static final String ACCOUNTS_FILE="accounts.txt";
    private static final ArrayList<Bankaccnt> accounts=new ArrayList<>();
    public static void main(String[] args) {
        loadAccounts();
        Scanner s;
        s = new Scanner(System.in);

        while (true) {
            System.out.print("Welcome to our Banking System \n please choose one of them to proceed..  \n 1.Create account\n 2.Login to existing account\n 3.Terminate\n\t Enter you choice:");
            int choice = s.nextInt();
            s.nextLine();//clearing the buffer

            switch (choice) {
                case 1: create_accnt(s);
                    break;
                case 2:
                    login_accnt(s);
                    break;
                case 3:
                    System.out.print("Thank you for using our Bank. Please visit soon..! ");
                    saveAccounts();
                    s.close();
                    return;
                default:
                    System.out.print("please select a valid input from above menu:");

            }

        }

    }



    private static void create_accnt(Scanner s){
        System.out.print("Enter you name:");
        String name=s.nextLine();
         System.out.print("Enter  4-digit pin:");
         int pin=s.nextInt();
         System.out.print("Please add intial deposit:");
         double deposit=s.nextDouble();
         s.nextLine();//clearing the buffer

         String acntnum="RBI00"+(accounts.size()+1);
         Bankaccnt newacnt=new Bankaccnt(name,acntnum,deposit,pin);
         accounts.add(newacnt);
         saveAccounts();
         System.out.println("\n\nAccount Created sucessfully:");
         System.out.println("Account name: "+name);
         System.out.println("Account number: "+acntnum);
     }

     private static void login_accnt(Scanner s){
        System.out.print("Enter your account details to login");
         System.out.print("Enter account number");
         String acntnum=s.nextLine();
         System.out.println("Enter your 4 digit pin:");
         int pin=s.nextInt();

         Bankaccnt loginacnt= accounts.stream().filter(
                 acc->acc.getAcntnum().equals(acntnum)&&(acc.getPin()==pin)
         ).findFirst().orElse(null);

         if(loginacnt !=null){
             System.out.println("Login Sucessful: \n Welcome " +loginacnt.getActName());
            handleAccounts(s,loginacnt);
         }
         else{
             System.out.println("invalid credentials!");
         }

     }

     private static void handleAccounts(Scanner s, Bankaccnt loginacnt){
        while(true){
            System.out.println("\n=== Account Menu ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. Log out");
            System.out.print("Enter your choice: ");

            int choice = s.nextInt();
            s.nextLine(); // Clear the buffer

            switch (choice) {
                case 1 -> loginacnt.check_bal();
                case 2 -> {
                    System.out.println("Enter amount to deposit: ");
                    double deposit = s.nextDouble();
                    loginacnt.deposit(deposit);
                    saveAccounts();
                }
                case 3 -> {
                    System.out.println("Enter amount to withdraw: ");
                    double withdraw = s.nextDouble();
                    loginacnt.withdraw(withdraw);
                    saveAccounts();
                }
                case 4 -> transferMoney(s, loginacnt);
                case 5 -> {
                    System.out.println("Logging out user "+ loginacnt.getActName()+ " ...!!!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        }
        private static void transferMoney(Scanner s, Bankaccnt sender){
        System.out.println("Enter recipents Account number:");
        String recp_acntnum=s.nextLine();
        System.out.println("Enter amount to transfer :");
        double amount=s.nextDouble();
        Bankaccnt recp=accounts.stream().filter(acc -> acc.getAcntnum().equals(recp_acntnum)).findFirst().orElse(null);
        if (recp!=null){
            sender.transfer(recp,amount);
            saveAccounts();
        }
        else{
            System.out.println("No user found with the account number given:"+recp_acntnum+" Kindly verify it again");
        }
        }
        private static void saveAccounts(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(ACCOUNTS_FILE))){
            for(Bankaccnt acnt:accounts){
                writer.write(acnt.toString());
                writer.newLine();
            }
        }
        catch(IOException e){
            System.out.print("Error in saving account details"+e.getMessage());
        }

        }
    private static void loadAccounts(){
        File file=new File(ACCOUNTS_FILE);
        if(!file.exists()) return;
        try (BufferedReader reader=new BufferedReader(new FileReader(file))){
            String line;
            while((line=reader.readLine())!=null){
                accounts.add(Bankaccnt.fromString(line));

            }
        }
        catch (IOException E){
            System.out.println("Error while Loading accounts"+E.getMessage());

        }
    }
}