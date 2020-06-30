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

// TODO: Modify servlet to access multiple messages from servlet.

package com.google.sps.servlets;

import java.util.*; 
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Class used to store comment data */
class Comment {
  String name;
  String commentText;

  /**
   * Constructor
   * @param name Author of comment
   * @param commentText Comment written by author
   */
  public Comment(String name, String commentText) {
    this.name = name;
    this.commentText = commentText;
  }
}

/** Servlet that writes an list of messages as a response. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  /** Hard coded messages used for tesing JSON */
  private List<Comment> messages;

  @Override
  public void init() {
    messages = new ArrayList<>();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = getParameter(request, /* name= */ "name-input", /* defaultValue= */ "");
    String commentText = getParameter(request, /* name= */ "comment-input", /* defaultValue= */ "");

    messages.add(new Comment(name, commentText));
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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.setContentType("text/html;");
    response.getWriter().println(json);
  }
}
