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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that writes messages as a response. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  /** Used to create Entity and its fields. */
  private static final String COMMENT = "Comment";
  private static final String TIME_STAMP = "timeStamp";
  private static final String NAME = "name";
  private static final String COMMENT_TEXT = "commentText";
  private static final String EMAIL = "email";
  
  /** Name of input field used for author name in comments section. */
  private static final String NAME_INPUT = "name-input";
  /** Name of input field used for comment text in comments section. */
  private static final String COMMENT_INPUT = "comment-input";
  /** Default value if comment section inputs are empty. */
  private static final String DEFAULT_VALUE = "";
  private static final String REDIRECT_URL = "/html/comments.html";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = getParameter(request, NAME_INPUT, DEFAULT_VALUE);
    String commentText = getParameter(request, COMMENT_INPUT, DEFAULT_VALUE);
    long timeStamp = System.currentTimeMillis();

    // Creates Entity and stores in database.
    Entity commentEntity = new Entity(COMMENT);
    commentEntity.setProperty(NAME, name);
    commentEntity.setProperty(EMAIL, getEmail());
    commentEntity.setProperty(COMMENT_TEXT, commentText);
    commentEntity.setProperty(TIME_STAMP, timeStamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect(REDIRECT_URL);
  }
  
  /**
   * Returns email of currently logged in user.
   */
  private String getEmail() {
    UserService userService = UserServiceFactory.getUserService();
    return userService.getCurrentUser().getEmail();
  }

  /**
   * Returns value with {@code name} from the {@code request} form. 
   * If the {@code name} cannot be found, return {@code defaultValue}.
   * @param request Form sent by client.
   * @param name {@code <input>} or {@code <textarea>} to read content of.
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(COMMENT).addSort(TIME_STAMP, SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty(NAME);
      String email = (String) entity.getProperty(EMAIL);
      String commentText = (String) entity.getProperty(COMMENT_TEXT);
      long timeStamp = (long) entity.getProperty(TIME_STAMP);
      
      // Creates new Comment for JSON accessibility.
      Comment newComment = Comment.create(id, name, email, commentText, timeStamp);
      comments.add(newComment);
    }

    Gson gson = new Gson();
    response.setContentType("text/html;");
    response.getWriter().println(gson.toJson(comments));
  }
}
