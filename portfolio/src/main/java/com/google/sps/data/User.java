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

package com.google.sps.data;

import com.google.auto.value.AutoValue;

/** Stores user login data. */
@AutoValue 
public abstract class User {
  public abstract boolean loggedIn();
  public abstract String loginUrl();
  public abstract String logoutUrl();

  /** 
   * Creates a User.
   * @param loggedIn Indicates if user is logged in.
   * @param loginUrl URL where user can login.
   * @param logoutUrl URL where user can logout.
   */
  public static User create(boolean loggedIn, String loginUrl, String logoutUrl) {
    return new AutoValue_User(loggedIn, loginUrl, logoutUrl);
  }
}