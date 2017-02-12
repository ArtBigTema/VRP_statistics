package av.VRP.rt;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Utils;
import av.VRP.rt.map.MapExample;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Created by Artem on 09.04.2016.
 */
public class MainFrame extends JFrame implements KeyListener {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTable tableTrips;
    private JPanel visualizationD;
    private JPanel visualizationH;
    private JPanel panel;
    private JButton dowloadLinksButton;
    private JButton dowloadLinkButton;
    private JProgressBar progressBar;
    private JList listLink;
    private JButton btn_statistic_days;
    private JProgressBar pb_calc_stat_days;
    private JProgressBar pb_read_file;
    private JButton btn_read_file;
    private JButton btn_statistic_hours;
    private JProgressBar pb_calc_stat_hours;
    private JPanel visualizationFH;
    private JPanel visualizationFD;
    private JList listForecastD;
    private JButton btnForecastD;
    private JButton btnForecastH;
    private JList listForecastH;
    private JList listForecastCoefH;
    private JButton btnForecastCoefH;
    private JList listForecastCoefD;
    private JButton btnForecastCoefD;
    private JButton exportButton;
    private JButton showPointsButton;
    private JSlider slider_zoom;
    private JButton startImitationButton;
    private JLabel messageLabel;
    private JLabel labelH;
    private JComboBox comboBoxShow;
    private JPanel maps1;
    private JPanel map_panel;
    private MapExample sample;

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

        tabbedPane1.getRootPane().setDefaultButton(dowloadLinksButton);
        tabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                switch (tabbedPane1.getSelectedIndex()) {
                    case 0:
                        tabbedPane1.getRootPane().setDefaultButton(dowloadLinkButton);
                        break;
                    case 1:
                        tabbedPane1.getRootPane().setDefaultButton(btn_read_file);
                        break;
                    case 2:
                        tabbedPane1.getRootPane().setDefaultButton(btn_statistic_hours);
                        break;
                    case 3:
                        tabbedPane1.getRootPane().setDefaultButton(btn_statistic_days);
                        break;
                    case 4:
                        tabbedPane1.getRootPane().setDefaultButton(btnForecastH);
                        break;
                    case 5:
                        tabbedPane1.getRootPane().setDefaultButton(btnForecastD);
                        break;
                    case 6:
                        tabbedPane1.getRootPane().setDefaultButton(startImitationButton);
                        break;
                }
            }
        });

        dowloadLinksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().aggregateList();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().export();
            }
        });
        dowloadLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().aggregateLink(getLinkFromList());
            }
        });

        btn_statistic_days.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pb_calc_stat_days.setVisible(true);
                btn_statistic_days.setVisible(false);
                Main.getInstance().aggregateStatisticForDay();
            }
        });

        btn_statistic_hours.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pb_calc_stat_hours.setVisible(true);
                btn_statistic_hours.setVisible(false);
                Main.getInstance().aggregateStatisticForHour();
            }
        });

        listLink.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                dowloadLinkButton.setEnabled(true);
            }
        });
        listForecastH.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listForecastD.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        listForecastH.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                btnForecastH.setEnabled(true);
                visualizationFH.removeAll();
                btnForecastCoefH.setEnabled(false);

                tabbedPane1.getRootPane().setDefaultButton(btnForecastH);
            }
        });

        listForecastD.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                btnForecastD.setEnabled(true);
                visualizationFD.removeAll();
                btnForecastCoefD.setEnabled(false);

                tabbedPane1.getRootPane().setDefaultButton(btnForecastD);
            }
        });

        btn_read_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pb_read_file.setVisible(true);
                btn_read_file.setVisible(false);
                btn_read_file.setEnabled(false);
                Main.getInstance().startParserThread();
            }
        });

        btnForecastH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listForecastH.getSelectedIndices().length < 2) {
                    JOptionPane.showMessageDialog(MainFrame.this, Constant.MSG_MORE_ONE);
                } else {
                    btnForecastH.setEnabled(false);
                    Main.getInstance().startForecastH(listForecastH.getSelectedIndices());

                    btnForecastCoefH.setEnabled(true);
                    tabbedPane1.getRootPane().setDefaultButton(btnForecastCoefH);
                }
            }
        });

        btnForecastD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listForecastD.getSelectedIndices().length < 2) {
                    JOptionPane.showMessageDialog(MainFrame.this, Constant.MSG_MORE_ONE);
                } else {
                    btnForecastD.setEnabled(false);
                    Main.getInstance().startForecastD(listForecastD.getSelectedIndices());//fixme for days

                    btnForecastCoefD.setEnabled(true);
                    tabbedPane1.getRootPane().setDefaultButton(btnForecastCoefD);
                }
            }
        });

        btnForecastCoefH.addActionListener(new ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   visualizationFH.removeAll();
                                                   Main.getInstance().showForecastGraphicFor(listForecastCoefH.getSelectedIndex(), true);
                                               }
                                           }
        );
        btnForecastCoefD.addActionListener(new ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   visualizationFD.removeAll();
                                                   Main.getInstance().showForecastGraphicFor(listForecastCoefD.getSelectedIndex(), false);
                                               }
                                           }
        );

        comboBoxShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().showPoints(sample, comboBoxShow.getSelectedIndex());
                slider_zoom.setVisible(false);
            }
        });

        slider_zoom.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Main.getInstance().zoom(slider_zoom.getValue());
            }
        });

        startImitationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startImitationButton.setVisible(false);
                slider_zoom.setVisible(true);
                comboBoxShow.setVisible(false);
                Main.getInstance().startImitation(sample);
            }
        });
        messageLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Main.getInstance().click();
                startImitationButton.setVisible(!startImitationButton.isVisible());
                slider_zoom.setVisible(!slider_zoom.isVisible());
                slider_zoom.setValue(slider_zoom.getMaximum());
                comboBoxShow.setVisible(!comboBoxShow.isVisible());
            }
        });

        sample = new MapExample();
        map_panel.add(sample);
    }

    public List getLinkFromList() {
        return listLink.getSelectedValuesList();
    }

    public void setListData(String[] listData) {
        listLink.setListData(listData);
        // listLink.setSelectedIndices(new int[]{6, 7});
        listLink.setSelectedIndex(7);
        dowloadLinksButton.setVisible(false);

        tabbedPane1.getRootPane().setDefaultButton(dowloadLinkButton);
    }

    public void startDownloading() {
        visualizationD.removeAll();
        visualizationH.removeAll();
        visualizationFH.removeAll();
        listLink.setEnabled(false);
        progressBar.setVisible(true);
        dowloadLinkButton.setEnabled(false);
        btn_statistic_days.setVisible(false);

        exportButton.setVisible(false);
        exportButton.setEnabled(false);

        comboBoxShow.setVisible(false);
        slider_zoom.setVisible(false);
    }

    public void endDownloading(boolean isSucces) {
        listLink.setEnabled(true);
        dowloadLinkButton.setEnabled(false);
        progressBar.setVisible(false);
        startImitationButton.setVisible(true);
        //   JOptionPane.showMessageDialog(this,
        //           isSucces ? "Succes" : "Fail", "Title",
        //           isSucces ? 1 : 0);//FIXME
    }

    public void setTableData(String[][] tableData) {
        DefaultTableModel model = (DefaultTableModel) tableTrips.getModel();
        for (String[] row : tableData) {
            model.addRow(row);
        }
        btn_statistic_days.setVisible(true);
        btn_statistic_hours.setVisible(true);
        pb_read_file.setVisible(false);

        exportButton.setVisible(true);
        exportButton.setEnabled(true);

        comboBoxShow.setVisible(true);
        slider_zoom.setVisible(true);
    }

    public void showGraphForDays(String days, Integer[][] dots, String[] month) {
        pb_calc_stat_days.setVisible(false);
        btn_statistic_days.setVisible(false);
        visualizationD.add(getGraphic(days, dots, month));
        this.repaint();
    }

    public void showGraphForHours(String days, Integer[][] dots, String[] day) {
        pb_calc_stat_hours.setVisible(false);
        btn_statistic_hours.setVisible(false);
        // visualizationH.add(getChart2DDemoK(days, dots, day));
        visualizationH.add(getGraphic(days, dots, day));

        this.repaint();
    }

    public void showGraphForForecastH(String days, Integer[][] dots, String[] day) {
        visualizationFH.add(getGraphic(days, dots, day));
        this.repaint();
    }

    public void showGraphForForecastD(String days, Integer[][] dots, String[] day) {
        visualizationFD.add(getGraphic(days, dots, day));
        this.repaint();
    }

    public void showPanelReadFile() {
        tabbedPane1.setSelectedIndex(1);

        btn_read_file.setVisible(true);
        btn_read_file.setEnabled(true);
        // btn_read_file.doClick();//FIXME remove
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

    public JLabel getLabelH() {
        return labelH;
    }

    public JPanel getGraphic(String dayOrHour, Integer[][] dots, String[] month) {
        final XYDataset dataset = createDataset(dots, month);
        final JFreeChart chart = createChart(dataset, dayOrHour);
        final ChartPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
    }

    private XYDataset createDataset(Integer[][] dots, String[] month) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int i = 0; i < dots.length; i++) {
            XYSeries s = new XYSeries(month[i]);
            for (int j = 0; j < dots[i].length; j++) {
                s.add(j + 1, dots[i][j]);
            }
            dataset.addSeries(s);
        }
        return dataset;

    }

    private JFreeChart createChart(final XYDataset dataset, String dayOrHour) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Статистика: " + dayOrHour,      // chart title
                dayOrHour,                      // x axis label
                "Загруженность",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        //  renderer.setSeriesLinesVisible(0, false);
        //  renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }


    public void setListForecast(String[] titles) {
        listForecastH.setListData(titles);//fixme if null or 0
        listForecastD.setListData(titles);//fixme if null or 0
        listForecastH.setSelectedIndices(new int[]{0, 1});
        listForecastD.setSelectedIndices(new int[]{0, 1});
        btnForecastH.setEnabled(true);
        btnForecastD.setEnabled(true);
    }

    public void setListCoefForecastH(String[] titles) {
        listForecastCoefH.setListData(titles);
        listForecastCoefH.setSelectedIndex(0);
    }

    public void setListCoefForecastD(String[] titles) {
        listForecastCoefD.setListData(titles);
        listForecastCoefD.setSelectedIndex(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {//FIXME
            dowloadLinkButton.doClick();
            btn_read_file.doClick();
        }
    }

    public void showMessage(String msg) {
        messageLabel.setText(msg);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void clickDownloadList() {
        dowloadLinksButton.doClick();
    }

    public void clickDownloadLink() {
        dowloadLinkButton.doClick();
    }

    public void clickReadFile() {
        btn_read_file.doClick();
    }

    public void showMap() {
        tabbedPane1.setSelectedIndex(6);
        showPoints();
    }

    public void showPoints() {
        //comboBoxShow.doClick();
    }

    public void setZoom(double zoom) {
        slider_zoom.setValue((int) zoom);
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
        tabbedPane1.addTab("Визуализация Hours", panel8);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel9.setEnabled(true);
        panel9.setVisible(true);
        panel8.add(panel9, BorderLayout.NORTH);
        btn_statistic_hours = new JButton();
        btn_statistic_hours.setText("Подсчитать статистику поездок по часам");
        btn_statistic_hours.setVisible(false);
        panel9.add(btn_statistic_hours, BorderLayout.CENTER);
        pb_calc_stat_hours = new JProgressBar();
        pb_calc_stat_hours.setVisible(false);
        panel9.add(pb_calc_stat_hours, BorderLayout.SOUTH);
        visualizationH = new JPanel();
        visualizationH.setLayout(new BorderLayout(0, 0));
        panel8.add(visualizationH, BorderLayout.CENTER);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Визуализация Days", panel10);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new BorderLayout(0, 0));
        panel10.add(panel11, BorderLayout.NORTH);
        btn_statistic_days = new JButton();
        btn_statistic_days.setEnabled(true);
        btn_statistic_days.setText("Подсчитать статистику поездок по дням");
        btn_statistic_days.setVisible(false);
        panel11.add(btn_statistic_days, BorderLayout.CENTER);
        pb_calc_stat_days = new JProgressBar();
        pb_calc_stat_days.setBorderPainted(false);
        pb_calc_stat_days.setIndeterminate(true);
        pb_calc_stat_days.setVisible(false);
        panel11.add(pb_calc_stat_days, BorderLayout.SOUTH);
        visualizationD = new JPanel();
        visualizationD.setLayout(new BorderLayout(0, 0));
        panel10.add(visualizationD, BorderLayout.CENTER);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Прогнозирование Hours", panel12);
        final JSplitPane splitPane1 = new JSplitPane();
        panel12.add(splitPane1, BorderLayout.CENTER);
        visualizationFH = new JPanel();
        visualizationFH.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(visualizationFH);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setOrientation(0);
        splitPane1.setLeftComponent(splitPane2);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new BorderLayout(0, 0));
        panel13.setMinimumSize(new Dimension(130, 150));
        splitPane2.setLeftComponent(panel13);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel13.add(scrollPane3, BorderLayout.CENTER);
        listForecastH = new JList();
        scrollPane3.setViewportView(listForecastH);
        btnForecastH = new JButton();
        btnForecastH.setEnabled(false);
        btnForecastH.setText("Прогнозирование");
        panel13.add(btnForecastH, BorderLayout.SOUTH);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new BorderLayout(0, 0));
        splitPane2.setRightComponent(panel14);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel14.add(scrollPane4, BorderLayout.CENTER);
        listForecastCoefH = new JList();
        listForecastCoefH.setSelectionMode(0);
        scrollPane4.setViewportView(listForecastCoefH);
        btnForecastCoefH = new JButton();
        btnForecastCoefH.setEnabled(false);
        btnForecastCoefH.setText("Показать отдельный график");
        panel14.add(btnForecastCoefH, BorderLayout.SOUTH);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Прогнозирование Days", panel15);
        final JSplitPane splitPane3 = new JSplitPane();
        panel15.add(splitPane3, BorderLayout.CENTER);
        visualizationFD = new JPanel();
        visualizationFD.setLayout(new BorderLayout(0, 0));
        splitPane3.setRightComponent(visualizationFD);
        final JSplitPane splitPane4 = new JSplitPane();
        splitPane4.setOrientation(0);
        splitPane3.setLeftComponent(splitPane4);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new BorderLayout(0, 0));
        splitPane4.setLeftComponent(panel16);
        final JScrollPane scrollPane5 = new JScrollPane();
        panel16.add(scrollPane5, BorderLayout.CENTER);
        listForecastD = new JList();
        scrollPane5.setViewportView(listForecastD);
        btnForecastD = new JButton();
        btnForecastD.setEnabled(false);
        btnForecastD.setText("Прогнозирование");
        panel16.add(btnForecastD, BorderLayout.SOUTH);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new BorderLayout(0, 0));
        splitPane4.setRightComponent(panel17);
        final JScrollPane scrollPane6 = new JScrollPane();
        panel17.add(scrollPane6, BorderLayout.CENTER);
        listForecastCoefD = new JList();
        listForecastCoefD.setSelectionMode(0);
        scrollPane6.setViewportView(listForecastCoefD);
        btnForecastCoefD = new JButton();
        btnForecastCoefD.setEnabled(false);
        btnForecastCoefD.setText("Показать отдельный график");
        panel17.add(btnForecastCoefD, BorderLayout.SOUTH);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}