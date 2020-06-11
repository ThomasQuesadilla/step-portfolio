google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('string', 'Animal');
  data.addColumn('number', 'Count');
  data.addRows([
    ['Dolphins', 15],
    ['Kangaroos', 20],
    ['Elephants', 5]
  ]);
  const options = {
    'title': 'Zoo Animals',
    'width': 400,
    'height': 500
  };
  const chart = new google.visualization.PieChart(document.getElementById           ('chart-container'));
  chart.draw(data, options);
}