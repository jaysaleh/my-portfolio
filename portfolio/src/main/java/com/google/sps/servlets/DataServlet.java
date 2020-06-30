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

/** Servlet that writes an list of messages as a response. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  /** Hard coded messages used for tesing JSON */
  private List<String> messages;

  @Override
  /** Initializes list of messages */
  public void init(){
    messages = new ArrayList<>();
    messages.add("one");
    messages.add("two");
    messages.add("three");
  }

  @Override
  /** Writes list of messages as JSON to the client.
   * @param request Information requested from servlet.
   * @param response Servlet writes information requested in response.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.setContentType("text/html;");
    response.getWriter().println(json);
  }
}
