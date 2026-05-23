import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import javax.security.auth.login.AccountNotFoundException;

class Account{
	private int ID;	
	private class Money {
		private long dollars;
		private int cents;
		public Money(String stringAmount) { // 0.00
			abortOnNull(stringAmount);
			int length = stringAmount.length();
			dollars = Long.parseLong(stringAmount.substring(0, length - 3));
			cents = Integer.parseInt(stringAmount.substring(length - 2, length));
		}
		public String getAmount() {
			if (cents > 9)
				return (dollars + "." + cents);
			else
				return (dollars + ".0" + cents);
		}
		public void addIn(Money secondAmount) {
			abortOnNull(secondAmount);
			int newCents = (cents + secondAmount.cents) % 100; // 60+50 = 110 % 100 = 10
			long carry = (cents + secondAmount.cents) / 100;
			cents = newCents;
			dollars = dollars + secondAmount.dollars + carry;
		}
		public void remove(Money withdrawAmount) {
	    	abortOnNull(withdrawAmount);
	    	if(cents < withdrawAmount.cents) {
	            int newCents = ((cents+100) - withdrawAmount.cents);
	            short remove = -1;
	            cents = newCents;
	            dollars = dollars - withdrawAmount.dollars + remove;
	    	}
	    	else {
	    		int newCents = (cents - withdrawAmount.cents); 
	            cents = newCents;
	            dollars = dollars - withdrawAmount.dollars;
	    	}
		}    	
		private void abortOnNull(Object o) {
			if (o == null) {
				System.out.println("Unexpected null argument.");
				System.exit(0);
			}
		}
		public void setDollars(long dollars) {
			this.dollars = dollars;
		}
		public void setCents(int cents) {
			this.cents = cents;
		}
	}
	private Money balance;
	private String name;
	private short pass;
	public Account(String name,short pass) { //making account cons
		Random r = new Random();
		ID = r.nextInt(999999)+4000000;
		balance= new Money("0.00");
		this.name=name;
		this.pass = pass;
	}
	public Account() {
		ID=-1;
		balance=new Money("0.00");
		name="0";
		pass = 9999;
	}
	public Account(int ID, String balance ,String name,short pass) { // loading accounts const
		this.ID=ID;
		this.balance=new Money(balance);
		this.name=name;
		this.pass = pass;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public Money getBalance() {
		return balance;
	}
	public String getBalanceString() {
		return balance.getAmount();
	}
	public void setBalance(String balance) {
		this.balance = new Money(balance);
	}
	public String toString() {
		return name + "'s Account [ ID = " + ID + ", balance = " + balance.getAmount() + "$ ]";
	}
	public boolean equals(Object obj) { // yazan.equals(youcef);
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (getClass() != obj.getClass())
			return false;
		else {
			Account other = (Account) obj;
			return ID == other.ID;
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void deposit(String depoValue) { // 50.00
		Money depo = new Money(depoValue);
		balance.addIn(depo);
	}
	public void withdraw(String drawValue) {
		Money draw = new Money(drawValue);
		if(draw.dollars<balance.dollars)
			balance.remove(draw);
		else
			throw new IllegalArgumentException("You dont have that amount of money </3");
	}
	public void transferFunds(Account toAccount, String transferValue) {
		Money transfer = new Money(transferValue);
		if (transfer.dollars > 0 || transfer.cents > 0 && transfer.dollars < balance.dollars) {
			toAccount.balance.addIn(transfer);
			balance.remove(transfer);
		}
		else {
			throw new IllegalArgumentException("You dont have this amound of money");
		}
	}
	public long getDollars() {
		return balance.dollars;
	}
	public int getCents() {
		return balance.cents;
	}
	public void setCents(int cents) {
		balance.setCents(cents);
	}
	public void setDollars(long dollars) {
		balance.setDollars(dollars);
	}
	public short getPass() {
		return pass;
	}
	public void setPass(short pass) {
		this.pass = pass;
	}

}
class SavingAccount extends Account {
	private double interestRate;
	public SavingAccount(String name,short pass, double interestRate) { // making account
		super(name,pass);
		this.interestRate = interestRate;
	}
	public SavingAccount(int ID, String balance ,String name,short pass,double interestRate) { // loading accounts const
		super(ID,balance,name,pass);
		this.interestRate=interestRate;
	}
	public double getIntrestRate() {
		return interestRate;
	}
	public void setIntrestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	public void addInterest() {
		double interest = (getDollars()+(getCents()/100.0)) * (interestRate/100); // 500.50
		 String temp = interest+"0";
		 deposit(temp);
	}
	public String toString() {
		return super.toString() + " type :- Saving Account";
	}
}
class CheckingAccount extends Account {
	private double overdraftLimit;
	public CheckingAccount(String name,short pass) {
		super(name,pass);
		overdraftLimit = 50;
	}
	public CheckingAccount(int ID, String balance ,String name,short pass,double overdraftLimit) { // loading accounts const
		super(ID,balance,name,pass);
		this.overdraftLimit=overdraftLimit;
	}
	public void withdraw(String drawValue) {
		Account temp = new Account();
		temp.setBalance(drawValue);
		if (temp.getDollars() > 0 && temp.getDollars() <= (getDollars() - overdraftLimit)) {
			super.withdraw(drawValue);
		} else {
			throw new IllegalArgumentException("Invalid withdraw amount, exceeds overdraft limit");
		}

	}
	public void transferFunds(Account toAccount, String transferValue) {
		Account temp = new Account();
		temp.setBalance(transferValue);
		if (temp.getDollars() > 0 || temp.getCents() > 0 && temp.getDollars() < (getDollars() - overdraftLimit)) {
			super.transferFunds(toAccount, transferValue);
		} else {
			throw new IllegalArgumentException("you dont have this amound of money / invaild transfer funds amount, exceeds overdraft limit ");
		}
	}
	public void editODL(double odl) {
		overdraftLimit = odl;
	}
	public double getODL() {
		return overdraftLimit;
	}
	public String toString() {
		return super.toString() + " type :- Checking Account";
	}
}
class Customer{
    private String name;
    private String customerID;
    private ArrayList<Account> accounts;
    public Customer(String name,String customerID) {
    	this.name=name;
    	this.customerID=customerID;
    	accounts=new ArrayList<Account>();
    }
    public Customer() {
    	name="";
    	customerID="-1";
    	accounts=new ArrayList<Account>();
    }
    public Customer(String name,String customerID,int accountsN) {//loading customers const
    	this.name=name;
    	this.customerID=customerID;
    	accounts=new ArrayList<Account>(accountsN);
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public ArrayList<Account> getAccounts() {
		ArrayList<Account> copy = (ArrayList)accounts.clone();
		return copy;
	}
	public void addAccount(Account account) {
		accounts.add(account);
	}
	public String toString() {
		return "Customer [ Name = " + name + ", ID = " + customerID + ", accounts = \n" + accounts + "\n ]";
	}
	
}
class WrongChoiceException extends Exception{
	public WrongChoiceException() {
		super("you have entered wrong choice !");
	}
	public WrongChoiceException(String message) {
		super(message);
	}
}
class RestartChoiceException extends Exception{
	public RestartChoiceException() {
		super("Restart Choice.");
	}
	public RestartChoiceException(String message) {
		super(message);
	}
}
class WrongPasswordException extends Exception{
	public WrongPasswordException() {
		super("you have entered wrong password !");
	}
	public WrongPasswordException(String message) {
		super(message);
	}
}
public class Bank {
	static ArrayList<Customer> customers = new ArrayList<Customer>();
	static ArrayList<Account> accounts = new ArrayList<Account>();
	public static void main(String[] args) throws ClassNotFoundException, IOException{
		try {
			loadAData("accounts.txt");
			loadCData("customers.txt");
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		boolean retry = true;
		while(retry) {
			try {
				int choice=-1,i =0;
				Scanner key = new Scanner(System.in);
				System.out.println("Type the number of your choice: \n1. Create Bank Account.\n2. Modify My Account. \n3. Login as Admin. \n4. Exit. ");
				choice = key.nextInt();
				key.nextLine();
				if(choice == 4) {
					saveCData("customers.txt");
					saveAData("accounts.txt");
					System.exit(0);

				}
				else if(choice == 1) {
					System.out.print("\033[H\033[2J"); // delete outs
					System.out.println("if you want Saving Account type number 1.");
					System.out.println("if you want Checking Account type number 2");
					choice = key.nextInt();
					key.nextLine();
					
					if(choice == 1) {
						System.out.println("Enter your (FullName), then your citizen ID :   (type 0 to cancel the procces)");
						String name = key.nextLine();
						if (name.equals("0")) {
							throw new RestartChoiceException();
						}
						String id = key.nextLine();
						
						int customerIndex = -1;
						for (i = 0; i < customers.size(); i++) {
							if(customers.get(i).getCustomerID().equals(id)) {
								customerIndex = i;
								break;
							}
						}
						
						if (customerIndex != -1) { 
							System.out.println("enter bank PIN password");
							short pass = key.nextShort();
							key.nextLine();
							System.out.println("enter interest rate");
							double interestRate = key.nextDouble();
							key.nextLine();
							SavingAccount t = new SavingAccount(name, pass, interestRate);
							accounts.add(t);
							customers.get(customerIndex).addAccount(t);
							System.out.println("your account id is : " + t.getID()); 
						} else { 
							customers.add(new Customer(name, id));
							customerIndex = customers.size() - 1;
							System.out.println("enter bank password ");
							short pass = key.nextShort();
							key.nextLine();
							System.out.println("enter interest rate ");
							double interestRate = key.nextDouble();
							key.nextLine();
							SavingAccount t = new SavingAccount(name, pass, interestRate);
							accounts.add(t);
							customers.get(customerIndex).addAccount(t);
							System.out.println("your account id is : " + t.getID()); 
						}
					}
					else if (choice==2) {
						System.out.println("Enter your (FullName), then your citizen ID :   (type 0 to cancel the procces)");
						String name = key.nextLine();
						if (name.equals("0")) {
							throw new RestartChoiceException();
						}
						String id = key.nextLine();
						
						int customerIndex = -1;
						for (i = 0; i < customers.size(); i++) {
							if(customers.get(i).getCustomerID().equals(id)) {
								customerIndex = i;
								break;
							}
						}
						
						if (customerIndex != -1) {
							System.out.println("enter bank password ");
							short pass = key.nextShort();
							key.nextLine();
							CheckingAccount t = new CheckingAccount(name, pass);
							accounts.add(t);
							customers.get(customerIndex).addAccount(t);
							System.out.println("your account id is : " + t.getID()); 
						} else {
							customers.add(new Customer(name, id));
							customerIndex = customers.size() - 1;
							System.out.println("enter bank password ");
							short pass = key.nextShort();
							key.nextLine();
							CheckingAccount t = new CheckingAccount(name, pass);
							accounts.add(t);
							customers.get(customerIndex).addAccount(t);
							System.out.println("your account id is : " + t.getID()); 
						}
					}
					else {
						throw new RestartChoiceException();
					}
				}
				else if (choice == 2) {
			        System.out.print("\033[H\033[2J"); // delete outs
			        System.out.println("Enter your account ID, then password (ID pass e.g \"1234567 1234\") :   if you want cancel the procces type 0.");
			        int id = key.nextInt();
			        key.nextLine();
			        if(id==0) {
			        	throw new RestartChoiceException();
			        }
			        short pass = key.nextShort();
					key.nextLine();
			        boolean searchAccount = false;
			        for (i=0;i<accounts.size()&&searchAccount==false;i++) {
						if(accounts.get(i).getID()==id&&accounts.get(i).getPass()==pass) {
							boolean account = true;
							System.out.println("You have entered your account correctly!");
							while(account) {
								searchAccount = true;
								System.out.println("Choose the process you want : \n1. Check balance \n2. Withdraw Money \n3. Deposit Money \n4. Transfer Money to another account \n5. Account Info \n6. Log Out. ");
								choice = key.nextInt();
								key.nextLine();
								if(choice == 6)
									 account = false;
								else if(choice==1) {
									System.out.println(accounts.get(i).getBalanceString());
									 account = true;
								}
								else if(choice==2) {
									System.out.println("Enter how much money you want withdraw like this pattern (40.00): ");
									String withdraw = key.nextLine();
									accounts.get(i).withdraw(withdraw);// maybe there is exception here..
									System.out.println("Done !");
									 account = true;
								}
								else if(choice==3) {
									System.out.println("Enter how much money you want deposit like this pattern (40.00) : ");
									String depo = key.nextLine();
									accounts.get(i).deposit(depo);
									System.out.println("Done !");
									 account = true;
								}
								else if(choice == 4) {
									int j;
									boolean found = false;
									System.out.println("Enter account ID you want transfer money to it then the money value (like this pattern \"40.00\"$)");
									int tID = key.nextInt(); // transfer account id 
									key.nextLine();
									String money = key.nextLine();
									for (j = 0; j < accounts.size(); j++) {
										if(tID==accounts.get(j).getID()) {
											found= true;
											break;
										}
									}
									if(found) {
										accounts.get(i).transferFunds(accounts.get(j), money);
										System.out.println("Done !");
									}
									
									else {
										throw new AccountNotFoundException("The account that you want transfer money to is not found !");
									}
								}
								else if(choice == 5) {
									System.out.println(accounts.get(i));
									 account = true;
								}
							}
						}
							
					}
			        if(searchAccount == false) {
			        	throw new AccountNotFoundException("please make sure you have entered correct password/account ID !");
			        }
				}
				else if (choice == 3) {
					System.out.print("\033[H\033[2J"); // delete outs
					System.out.println("Enter Admin Password : ");
					String pass = key.nextLine();
					if(pass.equals("PTUKBank2024")) {
						boolean admin = true;
						
						System.out.println("Adminstrator mode activated you are now access to everything !");
						while(admin) {
							System.out.println("Choose your operation : \n1. edit account name/ID. \n2. show account info \n"
									+ "3. add accounts intreset \n4. edit interests \n5. Delete account \n6. "
									+ "change overdraft limit \n7. total bank money \n8. number of customers and accounts \n9. exit");
							
							choice = key.nextInt();
							key.nextLine();
							if(choice == 1) {
								System.out.println("enter the current account id you want change its name or id");
								int id = key.nextInt();
								key.nextLine();
								boolean found = false;
								for (i = 0; i < accounts.size()&&found==false; i++) {
									if(accounts.get(i).getID()==id) {
										found = true;
										System.out.println("the account found enter the Name then ID : ");
										String newName = key.nextLine();
										int newID = key.nextInt();
										key.nextLine();
										accounts.get(i).setName(newName);
										accounts.get(i).setID(newID);
									}
								}
								if (found == false) {
									throw new AccountNotFoundException("You have entered wrong ID"); // exception

								}
							}
							else if(choice == 2) {
								boolean found = false;
								System.out.println("enter account id : ");
								int id=key.nextInt();
								key.nextLine();
								for (Account accs : accounts) {
									if(id==accs.getID()) {
										found = true;
										System.out.println(accs);
									}
								}
								if(found == false)
									throw new AccountNotFoundException("You have entered wrong ID"); // exception
							}
							else if(choice ==3) {
								for (i=0; i < accounts.size(); i++) {
									if(accounts.get(i)instanceof SavingAccount) {
										SavingAccount temp=(SavingAccount) accounts.get(i);
										temp.addInterest();
									}
								}
								System.out.println("interest added successfully");
							}
							else if(choice ==4) {
								boolean found=false;
								System.out.println("enter the id of the account and its new Interest Rate : ");
								int id = key.nextInt();
								key.nextLine();
								double ir= key.nextDouble();
								key.nextLine();
								for (i = 0; i < accounts.size()&&found==false; i++) {
									if(id==accounts.get(i).getID()&&accounts.get(i) instanceof SavingAccount) {
										SavingAccount temp = (SavingAccount)accounts.get(i);
										temp.setIntrestRate(ir);
										found = true;
									}
								}
								if(found)
								System.out.println("interest rate set successfully");
								else
									throw new AccountNotFoundException("You have entered wrong ID");
							}
							else if(choice == 5) {
								boolean found = false;
								System.out.println("enter account id you want delete : ");
								int id = key.nextInt();
								key.nextLine();
								for (i = 0; i < accounts.size()&&found==false; i++) {
									if(id==accounts.get(i).getID()) {
										accounts.remove(i);
										found = true;
									}
								}
								if(found)
									System.out.println("Accounts deleted");
									else
										throw new AccountNotFoundException("You have entered wrong ID");
							}
							else if (choice == 6) {
								boolean found=false;
								System.out.println("enter the id of the account and its new overdraft limit : ");
								int id = key.nextInt();
								key.nextLine();
								double odl= key.nextDouble();
								key.nextLine();
								for (i = 0; i < accounts.size()&&found==false; i++) {
									if(id==accounts.get(i).getID()&& accounts.get(i) instanceof CheckingAccount) {
										CheckingAccount temp = (CheckingAccount)accounts.get(i);
										temp.editODL(odl);
										found = true;
									}
								}
								if(found)
								System.out.println("overdraft limit set successfully");
								else
									throw new AccountNotFoundException("Please enter correct account id");
							}
							else if(choice == 7) {
								System.out.println("our bank has now : " + getTotalBankMoney() + "$");
							}
							else if (choice == 8) {
								i=0;
								for (Account account : accounts) {
									i++;
									System.out.println(i+". "+account);
								}
								i=0;
								for (Customer customer : customers) {
									i++;
									System.out.println(i+". "+customer);
								}
							}
							else if (choice == 9)
								  admin=false;
							else {
								throw new WrongChoiceException();
							}
						}
						
					}
					else
						throw new WrongPasswordException();
				}
				else
					throw new WrongChoiceException();
				
				
			}catch (InputMismatchException e) {
				System.out.println("you have entered wrong value");
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry = false;

			}
			catch (AccountNotFoundException e) {
				System.out.println(e.getMessage());
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry = false;
			}
			catch (StringIndexOutOfBoundsException e) {
				System.out.println("Please follow the pattern !");
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry=false;

			}
			catch (WrongChoiceException e) {
				System.out.println(e.getMessage());
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry = false;
			}
			catch (NoSuchElementException e) {
				System.out.println(e.getMessage());
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry=false;
			}
			catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry= false;
			}
			catch (WrongPasswordException e) {
				System.out.println(e.getMessage());
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry = true;
			}
			catch (RestartChoiceException e) {
				System.out.println(e.getMessage());
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry = true;
			}
			catch (FileNotFoundException e) {
				System.out.println("file not found !");
				System.out.println(e.getMessage());
				retry = false;
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				saveCData("customers.txt");
				saveAData("accounts.txt");
				retry = false;
			}
		}
		
	}
	public static void saveCData(String filename) throws IOException {
		PrintWriter delete = new PrintWriter(new FileOutputStream(filename)); // delete old file contents
    	delete.print("");
    	delete.close();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(filename))) {
        	for (Customer customer : customers) {
				pw.println(customer.getCustomerID() + " " + customer.getName() +" "+ customer.getAccounts().size());
			}
			pw.close();
        }
    }
	public static void loadCData(String filename) throws IOException, ClassNotFoundException {
        try (Scanner input = new Scanner(new FileInputStream(filename))) {
        	while (input.hasNext()) {
    				String ID = input.next();
    				String name = input.next();
    				int accountsN = input.nextInt();
    				Customer temp = new Customer(name,ID,accountsN);
    				customers.add(temp);
    		}
        	for (Customer customer : customers) { // customer [1,2,3] account[1,1,3] 
				for (Account account : accounts) {
					if (account.getName().equalsIgnoreCase(customer.getName())) {
						customer.addAccount(account);
					}
				}
			}
        }
    }
    public static void saveAData(String filename) throws IOException {
    	PrintWriter delete = new PrintWriter(new FileOutputStream(filename)); // delete old file contents
    	delete.println("");
    	delete.close();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(filename))) {
        	for (Account account : accounts) {
        		if(account instanceof SavingAccount) {
        			SavingAccount temp = (SavingAccount) account;
        			
        			pw.println("1 " + temp.getID()+ " " + temp.getBalanceString() + " " + temp.getName() + " " + temp.getPass() + " "+ temp.getIntrestRate());
        		}
        		else if(account instanceof CheckingAccount) {
        			CheckingAccount temp = (CheckingAccount) account;
        			pw.println("2 " + temp.getID()+ " " + temp.getBalanceString() + " " + temp.getName() + " " + temp.getPass() + " " + temp.getODL());
        		}
			}
			pw.close();
        }
    }
    public static void loadAData(String filename) throws IOException, ClassNotFoundException {
        try (Scanner input = new Scanner(new FileInputStream(filename))) {
        	while (input.hasNext()) {
    			int choice = input.nextInt();
    			if(choice==1) {
    				int ID = input.nextInt();
    				String balance = input.next();
    				String name = input.next();
    				short pass = (short)input.nextInt();
    				double interestRate = input.nextDouble();
    				SavingAccount temp = new SavingAccount(ID,balance,name,pass,interestRate);
    				accounts.add(temp);
    			}
    			else if (choice==2){
    				int ID = input.nextInt();
    				String balance = input.next();
    				String name = input.next();
    				short pass = (short)input.nextInt();
    				double odl = input.nextDouble();
    				CheckingAccount temp = new CheckingAccount(ID,balance,name,pass,odl);
    				accounts.add(temp);
    			}
    			
    		}
            
            
        }
    }
    public static String getTotalBankMoney() {
    	Account bank = new Account(202312871, "0.00", "Bank", (short)202312871);
    	for (Account account : accounts) {
			bank.deposit(account.getBalanceString());
		}
    	return bank.getBalanceString();
    }
    
}