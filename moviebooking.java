import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MovieTicketBooking extends JFrame implements ActionListener {
    JComboBox<String> movieBox, paymentBox;
    JLabel totalLabel, discountLabel;
    JTextField promoField;
    JButton calculateBtn, bookBtn, resetBtn, exitBtn;
    JPanel seatPanel;

    int pricePerTicket = 150;
    double finalAmount = 0.0;
    double discountAmount = 0.0;
    double promoDiscount = 0.0;
    JToggleButton[] seats = new JToggleButton[50];

    public MovieTicketBooking() {
        setTitle("Movie Ticket Booking");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        String[] movies = {"Select Movie", "salaar", "kgf2", "hi nanna", "jersey", "kalki 2898AD"};
        String[] payments = {"Select Payment Method", "UPI", "Credit Card", "Debit Card", "Cash"};

        // Top Panel
        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        topPanel.add(new JLabel("Select Movie:"));
        movieBox = new JComboBox<>(movies);
        topPanel.add(movieBox);

        topPanel.add(new JLabel("Payment Method:"));
        paymentBox = new JComboBox<>(payments);
        topPanel.add(paymentBox);

        topPanel.add(new JLabel("Promo Code:"));
        promoField = new JTextField();
        topPanel.add(promoField);

        topPanel.add(new JLabel("Discount Applied:"));
        discountLabel = new JLabel("₹0");
        topPanel.add(discountLabel);

        topPanel.add(new JLabel("Total Amount:"));
        totalLabel = new JLabel("₹0");
        topPanel.add(totalLabel);

        // Seat Panel
        seatPanel = new JPanel(new GridLayout(5, 10, 5, 5));
        seatPanel.setBorder(BorderFactory.createTitledBorder("Select Seats (50 seats total)"));
        for (int i = 0; i < 50; i++) {
            seats[i] = new JToggleButton("S" + (i + 1));
            seatPanel.add(seats[i]);
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        calculateBtn = new JButton("Calculate");
        bookBtn = new JButton("Book Ticket");
        resetBtn = new JButton("Reset");
        exitBtn = new JButton("Exit");

        calculateBtn.addActionListener(this);
        bookBtn.addActionListener(this);
        resetBtn.addActionListener(this);
        exitBtn.addActionListener(this);

        buttonPanel.add(calculateBtn);
        buttonPanel.add(bookBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(exitBtn);

        // Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(seatPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String selectedMovie = (String) movieBox.getSelectedItem();
        String selectedPayment = (String) paymentBox.getSelectedItem();
        String promoCode = promoField.getText().trim();

        if (e.getSource() == calculateBtn) {
            ArrayList<String> selectedSeats = getSelectedSeats();

            if (selectedMovie.equals("Select Movie") || selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a movie and at least one seat.");
            } else if (selectedPayment.equals("Select Payment Method")) {
                JOptionPane.showMessageDialog(this, "Please select a payment method.");
            } else {
                int seatCount = selectedSeats.size();
                double baseAmount = seatCount * pricePerTicket;
                discountAmount = getDiscountRate(selectedPayment) * baseAmount;
                promoDiscount = promoCode.equalsIgnoreCase("MOVIE50") ? 50 : 0;

                finalAmount = baseAmount - discountAmount - promoDiscount;
                if (finalAmount < 0) finalAmount = 0;

                discountLabel.setText("₹" + String.format("%.2f", discountAmount + promoDiscount));
                totalLabel.setText("₹" + String.format("%.2f", finalAmount));
            }
        }

        else if (e.getSource() == bookBtn) {
            ArrayList<String> selectedSeats = getSelectedSeats();

            if (totalLabel.getText().equals("₹0") || selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please calculate total before booking.");
            } else if (selectedPayment.equals("Select Payment Method")) {
                JOptionPane.showMessageDialog(this, "Please select a payment method.");
            } else {
                String bookingDetails =
                        "==== Booking Details ====\n" +
                        "Movie: " + selectedMovie +
                        "\nSeats: " + selectedSeats +
                        "\nPayment: " + selectedPayment +
                        "\nPromo Code: " + (promoField.getText().isEmpty() ? "N/A" : promoField.getText()) +
                        "\nDiscount: ₹" + String.format("%.2f", discountAmount + promoDiscount) +
                        "\nTotal: ₹" + String.format("%.2f", finalAmount) + "\n\n";

                JOptionPane.showMessageDialog(this, "Booking Confirmed!\n\n" + bookingDetails, "Success", JOptionPane.INFORMATION_MESSAGE);
                saveBookingToFile(bookingDetails);
            }
        }

        else if (e.getSource() == resetBtn) {
            movieBox.setSelectedIndex(0);
            paymentBox.setSelectedIndex(0);
            promoField.setText("");
            totalLabel.setText("₹0");
            discountLabel.setText("₹0");
            for (JToggleButton seat : seats) seat.setSelected(false);
        }

        else if (e.getSource() == exitBtn) {
            System.exit(0);
        }
    }

    private double getDiscountRate(String paymentMethod) {
        return switch (paymentMethod) {
            case "UPI" -> 0.10;
            case "Credit Card" -> 0.05;
            case "Debit Card" -> 0.02;
            default -> 0.0;
        };
    }

    private ArrayList<String> getSelectedSeats() {
        ArrayList<String> selected = new ArrayList<>();
        for (JToggleButton seat : seats) {
            if (seat.isSelected()) selected.add(seat.getText());
        }
        return selected;
    }

    private void saveBookingToFile(String data) {
        try (FileWriter writer = new FileWriter("booking_history.txt", true)) {
            writer.write(data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving booking history.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieTicketBooking::new);
    }
}