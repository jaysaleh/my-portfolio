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
import java.io.IOException;
import com.google.sps.data.Comment;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that writes an list of messages as a response. */
// TODO: Modify servlet to store values in database.
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  /** Name of input field used for author name in comments section */
  public static final String NAME_INPUT = "name-input";
  /** Name of input field used for comment text in comments section */
  public static final String COMMENT_INPUT = "comment-input";
  public static final String DEFAULT_VALUE = "";
  public static final String REDIRECT_URL = "/html/comments.html";

  /** Used to store Comments for JSON parsing */
  private List<Comment> messages;

  @Override
  public void init() {
    messages = new ArrayList<>();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = getParameter(request, NAME_INPUT, DEFAULT_VALUE);
    String commentText = getParameter(request, COMMENT_INPUT, DEFAULT_VALUE);

    messages.add(Comment.create(name, commentText));
    response.sendRedirect(REDIRECT_URL);
  }

  /**
   * Returns value with {@code name} from the {@code request} form. 
   * If the {@code name} cannot be found, return {@code defaultValue}.
   * @param request Form sent by client
   * @param name {@code textArea} to read content of
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
