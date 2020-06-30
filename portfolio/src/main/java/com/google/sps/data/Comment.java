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

package com.google.sps.data;

import com.google.auto.value.AutoValue;

/** Class used to create object to store comment data */
// TODO: Add database id feild for each comment.
@AutoValue public abstract class Comment {
  public abstract String name();
  public abstract String commentText();

  /** 
   * Creates comment object
   * @param name Name of author
   * @param commentText Text author wrote
   */
  public static Comment create(String name, String commentText) {
    return new AutoValue_Comment(name, commentText);
  }
}