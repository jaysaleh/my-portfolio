// Copyright 2020 Google LLC
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

import com.google.sps.data.User;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that retrieves user login status. */
@WebServlet("/user-login")
public class UserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService userService = UserServiceFactory.getUserService();
    
    Gson gson = new Gson();
    if (userService.isUserLoggedIn()) {
      String urlToRedirectToAfterUserLogsOut = "/html/comments.html";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      User newUser = User.create(/** loggedIn= */ true, /** loginURL= */ "", logoutUrl);
      response.getWriter().println(gson.toJson(newUser));
    } else {
      String urlToRedirectToAfterUserLogsIn = "/html/comments.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      User newUser = User.create(/** loggedIn= */ false, loginUrl, /** logoutURL= */ "");
      response.getWriter().println(gson.toJson(newUser));
    }
  }
}
