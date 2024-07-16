package fxml_helloworld;

public class DonationEntry {

    private String donorName;
    private double fundsIncoming;
    private String dateTransaction;

    public DonationEntry(String donorName, double fundsIncoming, String dateTransaction) {
        this.donorName = donorName;
        this.fundsIncoming = fundsIncoming;
        this.dateTransaction = dateTransaction;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public double getFundsIncoming() {
        return fundsIncoming;
    }

    public void setFundsIncoming(double fundsIncoming) {
        this.fundsIncoming = fundsIncoming;
    }

    public String getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(String dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

}
