package nilam.project.com.mortgagecalculator.model;


public class Loan {

    private double amount;
    private double downPayment;
    private float apr;
    private int term;

    public Loan(String amount, String downPayment, String apr, String term) {
        this.amount = Double.parseDouble(amount);
        this.downPayment = Double.parseDouble(downPayment);
        this.apr = Float.parseFloat(apr);
        this.term = Integer.parseInt(term);
    }

    public String calculateMonthlyPayment() {

        Double principal = amount - downPayment;
        // term in months
        term = term * 12;
        // monthly interest rate
        float rate = apr / 100 / 12;
        double payment = (principal * rate) / (1 - Math.pow(1 + rate, -term));

        return String.valueOf((double) Math.round(payment * 100) / 100);
    }

}