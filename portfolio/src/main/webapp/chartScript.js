google.charts.load('current', {'packages':['line']});
google.charts.setOnLoadCallback(drawStateChart);

function drawStateChart() {
  fetch('/chart-data').then(response => response.json())
  .then((covidCases) => {
    buildChart(covidCases[0], "state-chart-div");
    buildChart(covidCases[1], "county-chart-div");
  });
}

function buildChart(dataMap, divPath) {
  const data = new google.visualization.DataTable();
  data.addColumn('date', 'Date');
  data.addColumn('number', 'Cases');
  Object.keys(dataMap).forEach((day) => {
      data.addRow([new Date(day), dataMap[day]]);
    });
  const options = {
    'title': 'Daily Reported Covid Cases in Florida to June 11th',
    'width': 700,
    'height': 700
  };
  const chart = new google.charts.Line(document.getElementById(divPath));
  chart.draw(data, options);
}