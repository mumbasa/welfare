package com.welfare.repository;

import java.util.ArrayList;
import java.util.List;

public class ChartValues {
private List<String> labels;
private List<Double> figures;
public List<String> getLabels() {
	return labels;
}
public void setLabels(List<String> labels) {
	this.labels = labels;
}
public ChartValues() {
	labels= new ArrayList<String>();
	figures = new ArrayList<Double>();
	// TODO Auto-generated constructor stub
}
public List<Double> getFigures() {
	return figures;
}
public void setFigures(List<Double> figures) {
	this.figures = figures;
}

}
