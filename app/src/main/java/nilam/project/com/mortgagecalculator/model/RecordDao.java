package nilam.project.com.mortgagecalculator.model;


public class RecordDao {

    private int id;

    private String amount;
    private String downPayment;

    private String apr;
    private String term;

    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String type;

    private double latitude;
    private double longitude;

    private String monthlyPayment;

    public RecordDao() {
    }

    public RecordDao(String streetAddress, String city, String state, String zipcode, String type,
                     String amount, String downPayment, String apr, String term, double latitude,
                     double longitude, String monthlyPayment) {
        this.amount = amount;
        this.downPayment = downPayment;
        this.apr = apr;
        this.term = term;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.monthlyPayment = monthlyPayment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(String downPayment) {
        this.downPayment = downPayment;
    }

    public String getApr() {
        return apr;
    }

    public void setApr(String apr) {
        this.apr = apr;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(String monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

}
