// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;

/** Servlet to handle retrieval from and pass to comments **/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final static int DEFAULT_NUMBER_OF_COMMENTS = 10;
  private DatastoreService datastore;
  private int maxComments;
  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    maxComments = DEFAULT_NUMBER_OF_COMMENTS;
  }
  
  @Override 
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    maxComments = getMaxCommentChoice(request);
    
    ArrayList<Comment> comments = new ArrayList<>();
    for (Entity entity: results.asIterable()) {
      if (comments.size() < maxComments) {
        long id = entity.getKey().getId();
        long timestamp = (long) entity.getProperty("timestamp");
        String message = (String) entity.getProperty("message");

        Comment comment = new Comment(id, message, timestamp);
        comments.add(comment);
      }
    }

    response.setContentType("text/html;"); 
    Gson gson = new Gson(); 
    String json = gson.toJson(comments); 
    response.getWriter().println(json); 
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = getParameter(request, "text-input", "None");
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("message", text);
    commentEntity.setProperty("timestamp", timestamp);

    datastore.put(commentEntity);

    response.sendRedirect("/index.html?maxComments=" + maxComments);
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      value = defaultValue;
    }
    return value;
  }

  private int getMaxCommentChoice(HttpServletRequest request) {
    String numberChoice = request.getParameter("maxComments");
    int numberOfComments = DEFAULT_NUMBER_OF_COMMENTS;
    try {
      numberOfComments = Integer.parseInt(numberChoice);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numberChoice);
    }
    if (numberOfComments < 0) {
      numberOfComments = 0;
    }
    return numberOfComments;
  }
}