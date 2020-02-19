import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Random;

class Application {

    enum TriggerAction {
        SCROLL, MOVE
    }

    private TriggerAction triggerAction = TriggerAction.SCROLL;
    private long refreshInterval = 1000;
    private long timeToWait;
    private long defaultWaitTime = 60000;
    private long elapsed = 0;
    private boolean enabled;
    private JProgressBar progressBar;

    Application() {
        timeToWait = defaultWaitTime;
        JFrame frame = createFrame();
        try {
            Robot robot = new Robot();
            while (true) {
                Thread.sleep(refreshInterval);
                if (enabled) {
                    if (elapsed > timeToWait) {
                        onTriggered(robot);
                    }
                    elapsed += refreshInterval;
                    int percentage = (int) (elapsed * 100 / timeToWait + 0.5);
                    progressBar.setValue(percentage);
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with the robot!\n\n");
            e.printStackTrace();
        }
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame("Application");
        frame.setPreferredSize(new Dimension(300, 200));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JRadioButton enabledToggle = new JRadioButton();
        JTextField waitTimeField = new JTextField(String.valueOf(timeToWait));
        JButton saveButton = new JButton("Set New Wait Duration (Millis)");
        saveButton.setEnabled(false);

        saveButton.addActionListener((e) ->
        {
            try {
                timeToWait = Long.parseLong(waitTimeField.getText());
            } catch (NumberFormatException ex) {
                timeToWait = defaultWaitTime;
                waitTimeField.setText(String.valueOf(timeToWait));
                JOptionPane.showMessageDialog(null, "Invalid wait time was entered...");
            }

            saveButton.setEnabled(false);
        });

        waitTimeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void changedUpdate(DocumentEvent e) {
                saveButton.setEnabled(true);
            }

            @Override public void insertUpdate(DocumentEvent e) {
                saveButton.setEnabled(true);
            }

            @Override public void removeUpdate(DocumentEvent e) {
                saveButton.setEnabled(true);
            }
        });
        enabledToggle.addActionListener((e -> enabled = !enabled));

        JComboBox<TriggerAction> actionDropdown = new JComboBox<>(TriggerAction.values());

        actionDropdown.addItemListener(e -> triggerAction =
                TriggerAction.valueOf(actionDropdown.getSelectedItem().toString()));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel panel = new JPanel();
        JLabel enabledLabel = new JLabel("Enabled: ");
        JLabel dropdownLabel = new JLabel("Action: ");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(waitTimeField);
        panel.add(saveButton);
        panel.add(enabledLabel);
        panel.add(enabledToggle);
        panel.add(dropdownLabel);
        panel.add(actionDropdown);
        panel.add(progressBar);
        panel.setVisible(true);
        frame.add(panel);
        frame.pack();

        return frame;
    }

    private void onTriggered(Robot robot) {
        switch (triggerAction) {
            case SCROLL:
                robot.mouseWheel(1);
                robot.mouseWheel(-1);
                break;
            case MOVE:
                Point currPos = MouseInfo.getPointerInfo().getLocation();
                robot.mouseMove(currPos.x + 1, currPos.y + 1);
                robot.mouseMove(currPos.x, currPos.y);
                break;
        }
        elapsed = 0;
    }
}
