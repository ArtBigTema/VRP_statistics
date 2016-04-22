package av.VRP.rt;

import av.VRP.rt.Utils.Utils;

import net.sourceforge.chart2d.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

/**
 * Created by Artem on 09.04.2016.
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTable tableTrips;
    private JPanel visualization;
    private JPanel panel;
    private JButton dowloadLinksButton;
    private JButton dowloadLinkButton;
    private JProgressBar progressBar;
    private JList listLink;
    private JButton btn_statistic;
    private JProgressBar pb_calc_stat;
    private JProgressBar pb_read_file;
    private JButton btn_read_file;

    public MainFrame() {
        super("MainFrame");

        setContentPane(tabbedPane1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createUIComponents();
        this.setPreferredSize(new Dimension(900, 500));
        this.setMinimumSize(new Dimension(500, 500));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createUIComponents() {

        tableTrips.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        progressBar.setVisible(false);

        dowloadLinksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().aggregateList();
            }
        });

        dowloadLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().aggregateLink(getLinkFromList());
            }
        });

        btn_statistic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pb_calc_stat.setVisible(true);
                btn_statistic.setVisible(false);
                Main.getInstance().aggregateStatistic();
            }
        });

        listLink.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                dowloadLinkButton.setEnabled(true);
            }
        });

        btn_read_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pb_read_file.setVisible(true);
                btn_read_file.setVisible(false);
                Main.getInstance().startParserThread();
            }
        });
    }

    public List getLinkFromList() {
        return listLink.getSelectedValuesList();
    }

    public void setListData(String[] listData) {
        listLink.setListData(listData);
        dowloadLinksButton.setVisible(false);
    }

    public void startDownloading() {
        visualization.removeAll();
        listLink.setEnabled(false);
        progressBar.setVisible(true);
        dowloadLinkButton.setEnabled(false);
        btn_statistic.setVisible(false);
    }

    public void endDownloading(boolean isSucces) {
        listLink.setEnabled(true);
        dowloadLinkButton.setEnabled(false);
        progressBar.setVisible(false);

        JOptionPane.showMessageDialog(this,
                isSucces ? "Succes" : "Fail", "Title",
                isSucces ? 1 : 0);//FIXME
    }

    public void setTableData(String[][] tableData) {
        DefaultTableModel model = (DefaultTableModel) tableTrips.getModel();
        for (String[] row : tableData) {
            model.addRow(row);
        }
        btn_statistic.setVisible(true);
        pb_read_file.setVisible(false);
    }

    public void showGraph(String[][] days, Integer[][] dots, String[] month) {
        pb_calc_stat.setVisible(false);
        btn_statistic.setVisible(false);
        visualization.add(getChart2DDemoK(days, dots, month));
    }

    public void showPanelReadFile() {
        tabbedPane1.setSelectedIndex(1);

        btn_read_file.setVisible(true);
        btn_read_file.setEnabled(true);
        btn_read_file.doClick();//FIXME remove
    }

    public void setTableModel(boolean isShort) {//Case
        tableTrips.setModel(
                new DefaultTableModel(null, Utils.getTitleForTable(isShort)) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                }
        );
    }

    //FIXME move
    private Chart2D getChart2DDemoK(String[][] days, Integer[][] dots, String[] month) {
        LBChart2D chart = getStandartChard2D(month, days);

        Dataset dataset = new Dataset(dots.length, days[0].length, 1);

        for (int i = 0; i < dataset.getNumSets(); ++i) {
            for (int j = 0; j < dataset.getNumCats(); ++j) {
                for (int k = 0; k < dataset.getNumItems(); k++) {
                    dataset.set(i, j, k,
                            dots[i][j]);
                }
            }
        }
        chart.addDataset(dataset);
        //Optional validation:  Prints debug messages if invalid only.
        if (!chart.validate(false)) chart.validate(true);


        return chart;
    }

    public LBChart2D getStandartChard2D(String[] title, String[][] labels) {
        //<-- Begin Chart2D configuration -->

        //Configure object properties
        Object2DProperties object2DProps = new Object2DProperties();
        object2DProps.setObjectTitleText(title.toString());

        //Configure chart properties
        Chart2DProperties chart2DProps = new Chart2DProperties();
        chart2DProps.setChartDataLabelsPrecision(1);

        //Configure legend properties
        LegendProperties legendProps = new LegendProperties();
        String[] legendLabels = title;// {"2001", "2000", "1999"};
        legendProps.setLegendLabelsTexts(legendLabels);

        //Configure graph chart properties
        GraphChart2DProperties graphChart2DProps = new GraphChart2DProperties();
        String[] labelsAxisLabels = labels[0];//days 1 to 30(31)
        //   {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Nov", "Dec"};
        graphChart2DProps.setLabelsAxisLabelsTexts(labelsAxisLabels);
        graphChart2DProps.setLabelsAxisTitleText("Days");
        graphChart2DProps.setNumbersAxisTitleText("Count");
        graphChart2DProps.setLabelsAxisTicksAlignment(graphChart2DProps.CENTERED);

        //Configure graph properties
        GraphProperties graphProps = new GraphProperties();
        graphProps.setGraphBarsExistence(false);
        graphProps.setGraphLinesExistence(true);
        graphProps.setGraphLinesThicknessModel(2);
        graphProps.setGraphLinesWithinCategoryOverlapRatio(1f);
        graphProps.setGraphDotsExistence(true);
        //   graphProps.setGraphDotsThicknessModel(10);
        //  graphProps.setGraphDotsWithinCategoryOverlapRatio(1f);
        graphProps.setGraphAllowComponentAlignment(true);

        //Configure dataset


        //Configure graph component colors
        MultiColorsProperties multiColorsProps = new MultiColorsProperties();

        //Configure chart
        LBChart2D chart2D = new LBChart2D();
        chart2D.setObject2DProperties(object2DProps);
        chart2D.setChart2DProperties(chart2DProps);
        chart2D.setLegendProperties(legendProps);
        chart2D.setGraphChart2DProperties(graphChart2DProps);
        chart2D.addGraphProperties(graphProps);
        chart2D.addMultiColorsProperties(multiColorsProps);


        //<-- End Chart2D configuration -->

        return chart2D;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        tabbedPane1 = new JTabbedPane();
        panel1.add(tabbedPane1, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Загрузка данных", panel2);
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel2.add(panel, BorderLayout.CENTER);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel.add(panel3, BorderLayout.NORTH);
        dowloadLinksButton = new JButton();
        dowloadLinksButton.setText("DowloadLinks");
        panel3.add(dowloadLinksButton, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel.add(panel4, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel4.add(panel5, BorderLayout.SOUTH);
        dowloadLinkButton = new JButton();
        dowloadLinkButton.setEnabled(false);
        dowloadLinkButton.setText("DownloadLink");
        panel5.add(dowloadLinkButton, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        progressBar.setBorderPainted(false);
        progressBar.setIndeterminate(true);
        panel5.add(progressBar, BorderLayout.SOUTH);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, BorderLayout.CENTER);
        listLink = new JList();
        listLink.setFont(new Font(listLink.getFont().getName(), listLink.getFont().getStyle(), 14));
        listLink.setSelectionMode(2);
        scrollPane1.setViewportView(listLink);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Таблица данных", panel6);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setEnabled(true);
        scrollPane2.setVerifyInputWhenFocusTarget(true);
        scrollPane2.setVisible(true);
        panel6.add(scrollPane2, BorderLayout.CENTER);
        tableTrips = new JTable();
        scrollPane2.setViewportView(tableTrips);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        panel6.add(panel7, BorderLayout.NORTH);
        btn_read_file = new JButton();
        btn_read_file.setEnabled(false);
        btn_read_file.setText("Cчитать из файла");
        btn_read_file.setVisible(false);
        panel7.add(btn_read_file, BorderLayout.CENTER);
        pb_read_file = new JProgressBar();
        pb_read_file.setBorderPainted(false);
        pb_read_file.setIndeterminate(true);
        pb_read_file.setVisible(false);
        panel7.add(pb_read_file, BorderLayout.SOUTH);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Визуализация", panel8);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel8.add(panel9, BorderLayout.NORTH);
        btn_statistic = new JButton();
        btn_statistic.setEnabled(true);
        btn_statistic.setText("Подсчитать статистику");
        btn_statistic.setVisible(false);
        panel9.add(btn_statistic, BorderLayout.CENTER);
        pb_calc_stat = new JProgressBar();
        pb_calc_stat.setBorderPainted(false);
        pb_calc_stat.setIndeterminate(true);
        pb_calc_stat.setVisible(false);
        panel9.add(pb_calc_stat, BorderLayout.SOUTH);
        visualization = new JPanel();
        visualization.setLayout(new BorderLayout(0, 0));
        panel8.add(visualization, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}