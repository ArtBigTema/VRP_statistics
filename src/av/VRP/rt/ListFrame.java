package av.VRP.rt;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.HttpApi;
import av.VRP.rt.Utils.Utils;
import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.parser.ThreadWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Artem on 10.04.2016.
 */
public class ListFrame extends JFrame implements FileWriterListener {

    private JPanel panel;
    private JButton dowloadLinksButton;
    private JList listLink;
    private JButton dowloadLinkButton;
    private JProgressBar progressBar;

    public ListFrame() {
        super("MainFrame");

        $$$setupUI$$$();
        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createUIComponents();
        this.setPreferredSize(new Dimension(600, 500));
        this.setMinimumSize(new Dimension(600, 500));
        this.pack();
        this.setVisible(true);
    }

    private void createUIComponents() {
        progressBar.setVisible(false);
        dowloadLinksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aggregateList();
            }
        });
        dowloadLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aggregateLink();
            }
        });
    }

    private void aggregateLink() {
        ThreadWriter thread = new ThreadWriter(getLinkFromList());
        thread.setListener(this);
        thread.start();
    }


    public String getLinkFromList() {
        if (listLink.getSelectedValue() == null) {
            return Constant.URL_FIRST;
        } else {
            return (String) listLink.getSelectedValue();
        }
    }

    public void aggregateList() {
        progressBar.setVisible(true);
        String row = HttpApi.getInstance().getContent(Constant.URL_ALL);
        String[] rows = Utils.strToArray(row, "\n");

        listLink.setListData(rows);
        dowloadLinkButton.setEnabled(true);
        dowloadLinksButton.setEnabled(false);
        progressBar.setVisible(false);
    }

    @Override
    public void started() {
        progressBar.setVisible(true);
        dowloadLinksButton.setEnabled(false);
        dowloadLinkButton.setEnabled(false);
    }

    @Override
    public void stoped() {
        progressBar.setVisible(false);
        JOptionPane.showMessageDialog(this, "Succes", "Title", JOptionPane.INFORMATION_MESSAGE);//FIXME
        setVisible(false); //you can't see me!
        dispose(); //Destroy the JFrame object
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel.add(panel1, BorderLayout.NORTH);
        dowloadLinksButton = new JButton();
        dowloadLinksButton.setText("DowloadLinks");
        panel1.add(dowloadLinksButton, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel.add(panel2, BorderLayout.CENTER);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel2.add(panel3, BorderLayout.SOUTH);
        dowloadLinkButton = new JButton();
        dowloadLinkButton.setEnabled(false);
        dowloadLinkButton.setText("DownloadLink");
        panel3.add(dowloadLinkButton, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        progressBar.setBorderPainted(false);
        progressBar.setIndeterminate(true);
        panel3.add(progressBar, BorderLayout.SOUTH);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, BorderLayout.CENTER);
        listLink = new JList();
        listLink.setFont(new Font(listLink.getFont().getName(), listLink.getFont().getStyle(), 14));
        listLink.setSelectionMode(0);
        scrollPane1.setViewportView(listLink);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}