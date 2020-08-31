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

package com.google.sps;

import java.util.*; 
import java.util.Collection;
import com.google.sps.TimeRange;
import com.google.common.collect.Iterables;

/** 
 * Determines available time slots to schedule a meeting given
 * already scheduled meetings. 
 */
public final class FindMeetingQuery {
  
  /** 
   * Returns the open windows for which {@code request} can be scheduled in
   * while taking account conflicting meetings in {@code events}.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> requiredAttendees = request.getAttendees();
    Collection<TimeRange> availableTimes = assembleTime(events, requiredAttendees, request.getDuration());
    return adjustAvailableTimes;
  }

  /**
   * Returns the open windows for which {@code request} can be scheduled in,
   * after making {@code events}'' TimeRanges unavailable for scheduling.
   */
  public Collection<TimeRange> assembleTime(Collection<Event> events, Collection<String> requiredAttendees, long duration) {
    Collection<TimeRange> availableTimes = new ArrayList<>();

    // Initally, entire day is open to schedule a meeting in.
    availableTimes.add(TimeRange.WHOLE_DAY);

    // For every event that a requiredAttendee must attended, remove that TimeRange from availableTimes.
    for(Event currEvent : events) {
      if (!Collections.disjoint(currEvent.getAttendees(), requiredAttendees)) {
        availableTimes = adjustAvailableTimes(availableTimes, currEvent.getWhen());
      }
    }

    // Remove TimeRanges that are too short.
    availableTimes.removeIf(item -> item.duration() < duration);

    return availableTimes;
  }

  /**
   * Returns a collection of TimeRanges of TimeRanges in {@code availableTimes} that have been
   * adjusted to not include {@code eventWindow}.
   */
  public Collection<TimeRange> adjustAvailableTimes(Collection<TimeRange> availableTimes, TimeRange eventWindow) {
    Collection<TimeRange> newTimes = new ArrayList<>();
    for(TimeRange availableTime : availableTimes) {
      if (!availableTime.overlaps(eventWindow)) {
        newTimes.add(availableTime);
        continue;
      } 
      if (availableTime.contains(eventWindow)) {
        newTimes.add(TimeRange.fromStartEnd(availableTime.start(), eventWindow.start(), false));
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), availableTime.end(), false));
        continue;
      } 
      if (availableTime.start() > eventWindow.start() && availableTime.end() > eventWindow.end()) {
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), availableTime.end(), false));
        continue;
      }
      if (availableTime.start() > eventWindow.start() && eventWindow.start() > availableTime.end()) {
        newTimes.add(TimeRange.fromStartEnd(availableTime.start(), eventWindow.start(), false));
        continue;
      } 
    }
    return newTimes;
  }
}
