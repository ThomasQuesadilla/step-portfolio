package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Marker;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {

  DatastoreService datastore;

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    Collection<Marker> markers = getMarkers();

    response.getWriter().println(new Gson().toJson(markers));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    double latitude = Double.parseDouble(request.getParameter("latitude"));
    double longitude = Double.parseDouble(request.getParameter("longitude"));
    String content = Jsoup.clean(request.getParameter("content"), Whitelist.none());
    Marker marker = new Marker(latitude, longitude, content);
    storeMarker(marker);
  }

  private Collection<Marker> getMarkers() {
    Collection<Marker> markers = new ArrayList<>();
    Query query = new Query("Marker");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      double latitude = (double) entity.getProperty("latitude");
      double longitude = (double) entity.getProperty("longitude");
      String content = (String) entity.getProperty("content");

      Marker marker = new Marker(latitude, longitude, content);
      markers.add(marker);
    }
    return markers;
  }

  /** Stores a marker in Datastore. */
  public void storeMarker(Marker marker) {
    Entity markerEntity = new Entity("Marker");
    markerEntity.setProperty("latitude", marker.getLatitude());
    markerEntity.setProperty("longitude", marker.getLongitude());
    markerEntity.setProperty("content", marker.getContent());

    datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(markerEntity);
  }
}