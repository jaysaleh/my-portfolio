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

/** Stores data for comments written in portfolio. */
@AutoValue 
public abstract class Comment {
  public abstract long id();
  public abstract String name();
  public abstract String email();
  public abstract Optional<String> imageUrl();
  public abstract String commentText();
  public abstract long sentimentScore();
  public abstract long timeStamp();

  /** Returns a Builder for a Comment. */
  public static Builder builder() {
    return new AutoValue_Comment.Builder().setImageUrl(Optional.empty());
  }
  
  /** Builder object for Comment. */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setId(long id);
    public abstract Builder setName(String name);
    public abstract Builder setEmail(String email);
    public abstract Builder setImageUrl(Optional<String> imageUrl);
    public abstract Builder setCommentText(String commentText);
    public abstract Builder setSentimentScore(long sentimentScore);
    public abstract Builder setTimeStamp(long timeStamp);
    public abstract Comment build();
  }
}