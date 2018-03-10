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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import util.Sigma;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    private TextField pressure;
    @FXML
    private TextField temp;
    @FXML
    private TextField result;
    @FXML
    private TextField openOK;
    private XYChart.Series series;

    @FXML
    private ScatterChart<Double, Double> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private Map<Double, List<Double>> map;
    private List<Double> listY;
    private List<Double> listX;
    final private FileChooser fileChooser = new FileChooser();

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
        try {
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
        } catch (Exception ex) {
            openOK.setText(ex.toString());
        }
    }

    private ObservableList<XYChart.Series<Double,Double>> getChartData() {
        Double aValue = 10.0;
        double cValue = 17.06;
        ObservableList<XYChart.Series<Double, Double>> answer = FXCollections.observableArrayList();
        XYChart.Series<Double, Double> aSeries = new XYChart.Series<Double, Double>();
        //XYChart.Series<Double, Double> cSeries = new XYChart.Series<Double, Double>();
        aSeries.setName("a");

        for (int i = 0; i < listY.size(); i++) {
            aSeries.getData().add(new XYChart.Data<Double, Double>(listX.get(i), listY.get(i)));
            //aValue = aValue + 1;
            //cSeries.getData().add(new XYChart.Data(i, cValue));
            //cValue = cValue + Math.random() - .5;
        }

        answer.add(aSeries);

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
        this.lineChart.setLegendVisible(false);
        //this.lineChart.setCreateSymbols(false);

        this.xAxis.setAutoRanging(true);
        this.xAxis.setUpperBound(60);
        this.yAxis.setUpperBound(100D);
    }
}
