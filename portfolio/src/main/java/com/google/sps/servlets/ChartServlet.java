package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/chart-data")
public class ChartServlet extends HttpServlet {

  private LinkedHashMap<String, Integer> covidFloridaCases;
  private LinkedHashMap<String, Integer> covidBrowardMiamiDadeCases;

  @Override
  public void init() {
    covidFloridaCases = new LinkedHashMap<>();
    covidBrowardMiamiDadeCases = new LinkedHashMap<>();
    scanCSVFile("/WEB-INF/florida-covid19-daily.csv", covidFloridaCases);
    scanCSVFile("/WEB-INF/broward-miami-dade-daily-combined.csv", covidBrowardMiamiDadeCases);
    
  }

  private void scanCSVFile(String path, Map map) {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(
        path));
    scanner.nextLine();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      String date = cells[0];
      Integer cases = Integer.valueOf(cells[1]);

      map.put(date, cases);
    }
    scanner.close();
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getWriter().println("[" + new Gson().toJson(covidFloridaCases) + 
    "," + new Gson().toJson(covidBrowardMiamiDadeCases) +"]");
  }
}
