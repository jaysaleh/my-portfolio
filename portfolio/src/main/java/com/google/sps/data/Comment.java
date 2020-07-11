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

/** Stores data for comments written in portfolio. */
@AutoValue 
public abstract class Comment {
  public abstract long id();
  public abstract String name();
  public abstract String email();
  public abstract String commentText();
  public abstract long timeStamp();

  /** 
   * Creates a Comment.
   * @param id Id of object stored in database.
   * @param name Name of author.
   * @param email Email of user comment was written by.
   * @param commentText Text author wrote.
   * @param timeStamp Time when author submitted comment.
   */
  public static Comment create(long id, String name, String email, String commentText, long timeStamp) {
    return new AutoValue_Comment(id, name, email, commentText, timeStamp);
  }
}