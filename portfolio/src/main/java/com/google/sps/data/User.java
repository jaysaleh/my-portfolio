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
import java.util.Optional;

/** Stores user login data. */
@AutoValue 
public abstract class User {
  public abstract boolean loggedIn();
  public abstract Optional<String> loginUrl();
  public abstract Optional<String> logoutUrl();

  /** Returns a Builder for a User. */
  public static Builder builder() {
    return new AutoValue_User.Builder().setLoginUrl(Optional.empty()).setLogoutUrl(Optional.empty());
  }

  /** Builder object for User. */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setLoggedIn(boolean loggedIn);
    public abstract Builder setLoginUrl(Optional<String> loginUrl);
    public abstract Builder setLogoutUrl(Optional<String> logoutUrl);
    public abstract User build();
  }
}