package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import util.Sigma;
import util.trend.PolyTrendLine;
import util.trend.TrendLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    private TextField fvf;
    @FXML
    private TextField radius;
    @FXML
    private TextField h;
    @FXML
    private TextField viscosity;
    @FXML
    private TextField porosity;
    @FXML
    private TextField compresability;
    @FXML
    private TextField maxim;
    @FXML
    private TextArea result;
    @FXML
    private TextField openOK;
    private XYChart.Series series;

    @FXML
    private LineChart<Double, Double> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private Map<Double, List<Double>> map;
    private List<Double> listY;
    private List<Double> listX;
    private TrendLine t;
    final private FileChooser fileChooser = new FileChooser();
    private Double slope;
    private Double intercept;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            listX = Sigma.compute(map);
        } catch (Exception e) {
            result.setText(e.toString());
        }
    }

    @FXML
    protected void choseFile(ActionEvent actionEvent) {
        try {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                readFile(file);
            }
        } catch (Exception e) {
            openOK.setText(e.toString());
        }
    }
    @FXML
    protected void drawGraph(ActionEvent actionEvent) {
        //try {
            lineChart.setData(getChartData());
            for (final XYChart.Series<Double, Double> series : lineChart.getData()) {
                for (final XYChart.Data<Double, Double> data : series.getData()) {
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(String.format("X: %4.3f, Y:%4.3f",
                            data.getXValue(),
                            data.getYValue()));
                    Tooltip.install(data.getNode(), tooltip);
                }

            }
//        this.xAxis.setUpperBound(Collections.max(listX));
//        this.yAxis.setUpperBound(Collections.max(listY));
//        this.yAxis.setLowerBound(0.5
//        );


            Double permability = 162.6/slope*Double.parseDouble(viscosity.getText())*Double.parseDouble(fvf.getText())/Double.parseDouble(h.getText());
            Double skin = 1.151*(intercept/slope-Math.log10(permability/(Double.parseDouble(porosity.getText())*Double.parseDouble(viscosity.getText())*Double.parseDouble(compresability.getText())*Math.pow(Double.parseDouble(radius.getText()),2)))+3.23);
            result.setText(String.format("Results:%n slope=%4.3f, " +
                            "intercept=%4.3f, k = %4.3f md,%n skin = %4.3f",
                   slope, intercept, permability,skin));
    }

    private ObservableList<XYChart.Series<Double,Double>> getChartData() {
        t = new PolyTrendLine(1);


        Double[] xV = new Double[listX.size()];
        xV = listX.toArray(xV);
        Double[] yV = new Double[listY.size()];
        yV = listY.toArray(yV);

        double[] x=new double[Integer.parseInt(maxim.getText())];
        double[] y=new double[Integer.parseInt(maxim.getText())];
        System.out.println(listX.size()+"--"+Integer.parseInt(maxim.getText()));
        for (int i = 0; i < Integer.parseInt(maxim.getText()); i++) {
            x[i] = xV[i];
        }

        for (int i = 0; i < Integer.parseInt(maxim.getText()); i++) {
            y[i] = yV[i];
        }
        System.out.println(Arrays.toString(x));
        //double[] x = listX.stream().mapToDouble(Double::doubleValue).toArray(); //via method reference
        //double[] y = listY.stream().mapToDouble(Double::doubleValue).toArray(); //via method reference
        System.out.println(Arrays.toString(x));
        System.out.println(Arrays.toString(y));
        t.setValues(y,x);
        intercept = t.predict(0);
        slope = t.predict(2)-t.predict(1);
        ObservableList<XYChart.Series<Double, Double>> answer = FXCollections.observableArrayList();
        XYChart.Series<Double, Double> aSeries = new XYChart.Series<Double, Double>();
        XYChart.Series<Double, Double> cSeries = new XYChart.Series<Double, Double>();
        aSeries.setName("data");
        cSeries.setName(String.format("Trendline y = %4.3fx + %4.3f",slope,intercept));

        for (int i = 0; i < listY.size(); i++) {
            aSeries.getData().add(new XYChart.Data<Double, Double>(listX.get(i), listY.get(i)));
        }

        cSeries.getData().add(new XYChart.Data<Double, Double>(listX.get(Integer.parseInt(maxim.getText())-1), t.predict(listX.get(Integer.parseInt(maxim.getText())-1))));
        cSeries.getData().add(new XYChart.Data<Double, Double>(Collections.min(listX), t.predict(Collections.min(listX))));
        System.out.println(listX.get(listX.size()-1));
        answer.addAll(aSeries,cSeries);

        return answer;
    }

    private void readFile(File file) {
        map = new LinkedHashMap<Double, List<Double>>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            ArrayList<Double> list = new ArrayList<Double>();
            listY = new ArrayList<Double>();
            Double time, rate, y;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("\\s+");
                System.out.println(Arrays.toString(splitted));
                time = Double.parseDouble(splitted[0]);
                rate = Double.parseDouble(splitted[1]);
                y = Double.parseDouble(splitted[2]);
                listY.add(y);
                if (map.isEmpty()) {
                    map.put(rate, list);
                }
                if (map.containsKey(rate)) {
                    list.add(time);
                    map.put(rate, list);
                } else {
                    list = new ArrayList<Double>();
                    list.add(time);
                    map.put(rate, list);
                }
            }
        } catch (IOException ex) {
            openOK.setText(ex.toString());
        }
        openOK.setStyle("-fx-text-inner-color: green;");
        openOK.setText("Read successful!");
        System.out.println(map);
        System.out.println(listY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        XYChart.Series series = new XYChart.Series();
        this.lineChart.getData().add(series);
        this.lineChart.setLegendVisible(true);
        this.lineChart.setCreateSymbols(true);
        this.xAxis.setAutoRanging(true);
        this.yAxis.setAutoRanging(true);

    }
}
