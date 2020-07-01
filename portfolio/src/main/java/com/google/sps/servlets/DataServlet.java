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

import java.util.*; 
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that writes an list of messages as a response. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  public static final String COMMENT = "Comment";
  public static final String TIME_STAMP = "timeStamp";
  public static final String NAME = "name";
  public static final String COMMENT_TEXT = "commentText";

  /** Creates a "comment" entity and stores in database */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = getParameter(request, /* textArea name= */ "name-input", /* defaultValue= */ "");
    String commentText = getParameter(request, /* textArea name= */ "comment-input", /* defaultValue= */ "");
    long timeStamp = System.currentTimeMillis();

    Entity commentEntity = new Entity(COMMENT);
    commentEntity.setProperty(NAME, name);
    commentEntity.setProperty(COMMENT_TEXT, commentText);
    commentEntity.setProperty(TIME_STAMP, timeStamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/html/comments.html");
  }

  /**
   * Gets value from form in comments section of portfolio and returns it. If form is empty,
   * function will return defaultValue passed in.
   * @param request Form sent by client
   * @param name textArea to retrive information from
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Queries database for comments and writes them to {@code response} as JSON. 
   * Comments are written in order of most recent time stamp.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(COMMENT).addSort(TIME_STAMP, SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty(NAME);
      String commentText = (String) entity.getProperty(COMMENT_TEXT);
      long timeStamp = (long) entity.getProperty(TIME_STAMP);
      
      // Creates new comment object for JSON accessibility
      Comment newComment = Comment.create(id, name, commentText, timeStamp);
      comments.add(newComment);
    }

    Gson gson = new Gson();
    response.setContentType("text/html;");
    response.getWriter().println(gson.toJson(comments));
  }
}
