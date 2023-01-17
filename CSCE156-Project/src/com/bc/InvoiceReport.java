package com.bc;

import java.util.ArrayList;
import java.util.List;

import com.bc.ext.DatabaseParser;
import com.bc.ext.InvoiceData;
import com.bc.models.*;

public class InvoiceReport {
    public static void main(String[] args) {

        ArrayList<Person> people = Parser.parsePersonsList();
        ArrayList<Customer> customers = Parser.parseCustomerList(people);
        ArrayList<Product> products = Parser.parseProductsList();
        ArrayList<Invoice> invoices = Parser.parseInvoiceList(people, customers, products);

        for (Person person : people) {
            InvoiceData.addPerson(person.getCode(), person.getFirstName(), person.getLastName(), person.getAddress().getStreet(), person.getAddress().getCity(), person.getAddress().getState(), person.getAddress().getZip(), person.getAddress().getCountry());
            StringBuilder emailString = new StringBuilder();
            for (String email : person.getEmails()) {
                emailString.append(email + ",");
            }
            InvoiceData.addEmail(person.getCode(), emailString.toString());
        }

        for (Customer customer : customers) {
            InvoiceData.addCustomer(customer.getCode(), customer.getType(), customer.getPrimaryContact().getCode(), customer.getName(), customer.getAddress().getStreet(), customer.getAddress().getCity(), customer.getAddress().getState(), customer.getAddress().getZip(), customer.getAddress().getCountry());
        }
        for (Product product : products) {
            String type = product.getType();
            if (type.equals("R")) {
                Rental rental = (Rental) product;
                InvoiceData.addRental(rental.getCode(), rental.getLabel(), rental.getDailyCost(), rental.getDeposit(), rental.getCleaningFee());
            } else if (type.equals("F")) {
                Repair repair = (Repair) product;
                InvoiceData.addRepair(repair.getCode(), repair.getLabel(), repair.getPartsCost(), repair.getLaborRate());
            } else if (type.equals("C")) {
                Concession concession = (Concession) product;
                InvoiceData.addConcession(concession.getCode(), concession.getLabel(), concession.getCost());
            } else if (type.equals("T")) {
                Towing towing = (Towing) product;
                InvoiceData.addTowing(towing.getCode(), towing.getLabel(), towing.getCostPerMile());
            }
        }
        for (Invoice invoice : invoices) {
            InvoiceData.addInvoice(invoice.getInvoiceCode(), invoice.getPerson().getCode(), invoice.getCustomer().getCode());
            List<Purchase> purchaseList = invoice.getPurchaseList();
            for (Purchase purchase : purchaseList) {
                String type = purchase.getProduct().getType();
                Product product = purchase.getProduct();
                if (type.equals("R")) {
                    RentalPurchase rental = (RentalPurchase) purchase;
                    InvoiceData.addRentalToInvoice(invoice.getInvoiceCode(), product.getCode(), rental.getDaysRented());
                } else if (type.equals("F")) {
                    RepairPurchase repair = (RepairPurchase) purchase;
                    InvoiceData.addRepairToInvoice(invoice.getInvoiceCode(), product.getCode(), repair.getHoursWorked());
                } else if (type.equals("C")) {
                    ConcessionPurchase concession = (ConcessionPurchase) purchase;
                    InvoiceData.addConcessionToInvoice(invoice.getInvoiceCode(), product.getCode(), concession.getQuantity(), concession.getAssocRepair());
                } else if (type.equals("T")) {
                    TowingPurchase towing = (TowingPurchase) purchase;
                    InvoiceData.addTowingToInvoice(invoice.getInvoiceCode(), product.getCode(), towing.getMilesTowed());
                }
            }
        }

        ArrayList<Person> personList = DatabaseParser.parsePersonList();
        ArrayList<Customer> customerList = DatabaseParser.parseCustomerList(personList);
        ArrayList<Product> productList = DatabaseParser.parseProductList();
        ArrayList<Invoice> invoiceList = DatabaseParser.parseInvoiceList(personList, customerList, productList);
        GenericList<Invoice> invLinkedList = new GenericList<>();
        for (Invoice inv : invoiceList) {
            invLinkedList.addItemSorted(inv);
        }

        System.out.println("Executive Summary Report:");
        System.out.println("=========================");
        System.out.printf("%-10s %-20s %-20s %11s %11s %11s %11s %11s\n", "Code", "Owner", "Customer Account", "Subtotal", "Discounts", "Fees", "Taxes", "Total");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        float subtotal = 0, fees = 0, taxes = 0, discount = 0, total = 0;
        float subtotalTotals = 0, feesTotals = 0, taxesTotals = 0, discountTotals = 0, totalTotals = 0;
        for (Invoice inv : invLinkedList) {
            System.out.printf("%-10s %-20s %-20s", inv.getInvoiceCode(), inv.getPerson().getLastFirstName(), inv.getCustomer().getName());
            subtotal = inv.calculateSubTotal();
            fees = inv.calculateFees();
            taxes = inv.calculateTax();
            discount = inv.calculatePreTaxDiscounts() + inv.calculatePostTaxDiscounts();
            total = inv.calculateTotalCost();
            subtotalTotals += subtotal;
            feesTotals += fees;
            taxesTotals += taxes;
            discountTotals += discount;
            totalTotals += total;
            System.out.printf(" $%10.2f $%10.2f $%10.2f $%10.2f $%10.2f\n", subtotal, -discount, fees, taxes, total);
        }
        System.out.println("==================================================================================================================");
        System.out.printf("%-52s $%10.2f $%10.2f $%10.2f $%10.2f $%10.2f\n\n\n\n", "TOTALS", subtotalTotals, -discountTotals, feesTotals, taxesTotals, totalTotals);

        System.out.println("Invoice Details:");
        for (Invoice inv : invLinkedList) {
            System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
            System.out.println("Invoice " + inv.getInvoiceCode());
            System.out.println("------------------------------------------");

            System.out.println("Owner:");
            System.out.println("\t" + inv.getPerson().getLastFirstName());
            System.out.println("\t" + "[" + inv.getPerson().printEmails() + "]");
            System.out.println("\t" + inv.getPerson().getAddress().getStreet());
            System.out.println("\t" + inv.getPerson().getAddress().getCity() + ", " + inv.getPerson().getAddress().getState()
                    + ", " + inv.getPerson().getAddress().getCountry() + " " + inv.getPerson().getAddress().getZip());

            System.out.println("Customer:");
            System.out.printf("\t%s", inv.getCustomer().getName());
            if (inv.getCustomer().getType().equals("B")) {
                System.out.printf(" (Business Account)\n");
            } else {
                System.out.printf(" (Personal Account)\n");
            }
            System.out.println("\t" + inv.getCustomer().getAddress().getStreet());
            System.out.println("\t" + inv.getCustomer().getAddress().getCity() + ", " + inv.getCustomer().getAddress().getState()
                    + ", " + inv.getCustomer().getAddress().getCountry() + " " + inv.getCustomer().getAddress().getZip());

            System.out.println("Product:");
            System.out.printf("  %-10s %10s %60s %10s %10s %10s\n", "Code", "Description", "Subtotal", "Discount", "Tax", "Total");
            System.out.println("  ------------------------------------------------------------------------------------------------------------------------");

            float invTotal = 0;
            float taxRate = (float) 0.08;
            if (inv.getCustomer().getType().equals("B")) {
                taxRate = (float) 0.0425;
            }

            for (Purchase p : inv.getPurchaseList()) {
                String code = p.getProduct().getCode();
                String description1 = "";
                String description2 = "";
                float purchaseDiscount = 0;

                if (p.getProduct().getType().equals("R")) {
                    description1 += p.getProduct().getLabel() + " (" + ((RentalPurchase) p).getDaysRented() + " day(s) @ $"
                            + ((Rental) p.getProduct()).getDailyCost() + "/day)";
                    description2 += "(+ $" + ((Rental) p.getProduct()).getCleaningFee() + " cleaning fee, -$"
                            + ((Rental) p.getProduct()).getDeposit() + " deposit refund)";
                } else if (p.getProduct().getType().equals("F")) {
                    description1 += p.getProduct().getLabel() + " (" + ((RepairPurchase) p).getHoursWorked() + " hour(s) of labor @ $"
                            + ((Repair) p.getProduct()).getLaborRate() + "/hour)";
                    description2 += "(+ $" + ((Repair) p.getProduct()).getPartsCost() + " for parts)";
                } else if (p.getProduct().getType().equals("C")) {
                    description1 += p.getProduct().getLabel() + " (" + ((ConcessionPurchase) p).getQuantity() + " unit(s) @ $"
                            + ((Concession) p.getProduct()).getCost() + "/unit)";
                    if (((ConcessionPurchase) p).getAssocRepairDiscount()) {
                        purchaseDiscount += ((ConcessionPurchase) p).getPurchaseCost() * 0.1;
                    }

                } else if (p.getProduct().getType().equals("T")) {
                    description1 += p.getProduct().getLabel() + " (" + ((TowingPurchase) p).getMilesTowed() + " mile(s) @ $"
                            + ((Towing) p.getProduct()).getCostPerMile() + "/mile)";

                    int towingFlag = 0, repairFlag = 0, rentalFlag = 0;
                    for (Purchase purch : inv.getPurchaseList()) {
                        if (purch.getProduct().getType().equals("T")) {
                            towingFlag = 1;
                        }
                        if (purch.getProduct().getType().equals("R")) {
                            repairFlag = 1;
                        }
                        if (purch.getProduct().getType().equals("F")) {
                            rentalFlag = 1;
                        }
                    }
                    int freeTowing = towingFlag + repairFlag + rentalFlag;
                    if (freeTowing == 3) {
                        purchaseDiscount += ((TowingPurchase) p).getPurchaseCost();
                    }
                }

                float purchaseTax = (p.getPurchaseCost() - purchaseDiscount) * taxRate;
                float purchaseTotal = p.getPurchaseCost() - purchaseDiscount + purchaseTax;
                invTotal += purchaseTotal;

                System.out.printf("  %-10s %-60s $%10.2f $%10.2f $%10.2f $%10.2f\n", code, description1, p.getPurchaseCost(), -purchaseDiscount, purchaseTax, purchaseTotal);
                if (p.getProduct().getType().equals("F") || p.getProduct().getType().equals("R")) {
                    System.out.printf("             %s\n", description2);
                }

            }
            System.out.println("===========================================================================================================================");
            System.out.printf("%-73s $%10.2f $%10.2f $%10.2f $%10.2f\n", "ITEM TOTALS", inv.calculateSubTotal(), -inv.calculatePreTaxDiscounts(), inv.calculateTax(), invTotal);
            if (inv.getCustomer().getType().equals("B")) {
                System.out.printf("%-109s $%10.2f\n", "BUSINESS ACCOUNT FEE", 75.50);
            }
            if (inv.calculatePostTaxDiscounts() > 0) {
                System.out.printf("%-109s $%10.2f\n", "LOYAL CUSTOMER DISCOUNT (5% OFF)", -inv.calculatePostTaxDiscounts());
            }
            System.out.printf("%-109s $%10.2f\n", "GRAND TOTAL", inv.calculateTotalCost());

            System.out.printf("\n\n\t\t%s\n\n\n", "THANK YOU FOR DOING BUSINESS WITH US!");
        }
    }

}

