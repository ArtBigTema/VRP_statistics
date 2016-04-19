package av.VRP.rt;

import av.VRP.rt.Utils.Constant;
import net.sourceforge.chart2d.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by Artem on 09.04.2016.
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton btn_download_data;
    private JTextArea ta_data_output;
    private JTable tableTrips;
    private JPanel visualization;

    public MainFrame() {
        super("MainFrame");

        setContentPane(tabbedPane1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        createUIComponents();
        this.setPreferredSize(new Dimension(900, 500));
        this.setMinimumSize(new Dimension(500, 500));
        this.pack();
        this.setVisible(true);
        tabbedPane1.setSelectedIndex(2);//FIXME remove
    }

    private void createUIComponents() {
        btn_download_data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ta_data_output.append("test \n");
            }
        });
        tableTrips.setModel(
                new DefaultTableModel(null, Constant.TABLE_TITLES) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                }
        );
        tableTrips.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private Chart2D getChart2DDemoK(String[] days, Long[] dots, String month) {
        LBChart2D chart = getStandartChard2D(month, days);

        Dataset dataset = new Dataset(1, days.length, 1);

        for (int i = 0; i < dataset.getNumSets(); ++i) {
            for (int j = 0; j < dataset.getNumCats(); ++j) {
                for (int k = 0; k < dataset.getNumItems(); k++) {
                    dataset.set(i, j, k,
                            dots[j].floatValue());
                }
            }
        }
        chart.addDataset(dataset);
        //Optional validation:  Prints debug messages if invalid only.
        if (!chart.validate(false)) chart.validate(true);


        return chart;
    }

    public LBChart2D getStandartChard2D(String title, String[] labels) {
        //<-- Begin Chart2D configuration -->

        //Configure object properties
        Object2DProperties object2DProps = new Object2DProperties();
        object2DProps.setObjectTitleText(title);

        //Configure chart properties
        Chart2DProperties chart2DProps = new Chart2DProperties();
        chart2DProps.setChartDataLabelsPrecision(1);

        //Configure legend properties
        LegendProperties legendProps = new LegendProperties();
        String[] legendLabels = new String[]{title};// {"2001", "2000", "1999"};
        legendProps.setLegendLabelsTexts(legendLabels);

        //Configure graph chart properties
        GraphChart2DProperties graphChart2DProps = new GraphChart2DProperties();
        String[] labelsAxisLabels = labels;
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

    public void showData(Object row) {
        ta_data_output.append(row.toString() + "\n");
    }

    public synchronized void addRow(String[] row) {
        DefaultTableModel model = (DefaultTableModel) tableTrips.getModel();
        model.addRow(row);
    }

    public void showGraph(String[] days, Long[] dots, String month) {
        visualization.add(getChart2DDemoK(days, dots, month));
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
        btn_download_data = new JButton();
        btn_download_data.setText("Dowload data");
        panel2.add(btn_download_data, BorderLayout.NORTH);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setVerticalScrollBarPolicy(22);
        panel2.add(scrollPane1, BorderLayout.CENTER);
        ta_data_output = new JTextArea();
        ta_data_output.setMinimumSize(new Dimension(100, 107));
        ta_data_output.setText("");
        scrollPane1.setViewportView(ta_data_output);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Таблица данных", panel3);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setEnabled(true);
        scrollPane2.setVerifyInputWhenFocusTarget(true);
        panel3.add(scrollPane2, BorderLayout.CENTER);
        tableTrips = new JTable();
        scrollPane2.setViewportView(tableTrips);
        visualization = new JPanel();
        visualization.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Визуализация", visualization);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}