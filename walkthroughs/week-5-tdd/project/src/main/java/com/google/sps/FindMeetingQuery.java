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
 * Determines a list of available TimeRanges to schedule a meeting
 * given a MeetingRequest and list of events taking place that day. 
 */
public final class FindMeetingQuery {
  
  /** 
   * Returns the open windows for which {@code request} can be scheduled in.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> requiredAttendees = new ArrayList<>(request.getAttendees());
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    Collection<String> allAttendees = new ArrayList<>(requiredAttendees);
    allAttendees.addAll(optionalAttendees);

    // Treat optional attendees as required and see if there are available times.
    Collection<TimeRange> availableTimes = assembleTime(events, allAttendees, request.getDuration());
    if (requiredAttendees.isEmpty() || !availableTimes.isEmpty()){
      return availableTimes;
    }
    // Return times for required attendees only.
    return assembleTime(events, requiredAttendees, request.getDuration());
  }

  /**
   * Returns the open windows for which {@code request} can be scheduled in,
   * after making {@code events} TimeRanges unavailable in {@code availableTimes}.
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
   * Returns {@code newTimes} which takes all TimeRanges in {@code availableTimes} and
   * adjusts them to not include {@code eventWindow}.
   */
  public Collection<TimeRange> adjustAvailableTimes(Collection<TimeRange> availableTimes, TimeRange eventWindow) {
    Collection<TimeRange> newTimes = new ArrayList<>();
    for(TimeRange availableTime : availableTimes) {
      // If timeTime doesn't overlap with eventWindow then availableTime is available.
      if (!availableTime.overlaps(eventWindow)) {
        newTimes.add(availableTime);
        continue;
      } 
      // If eventWindow fits entirely into availableTime then split availableTime into two windows.
      if (availableTime.contains(eventWindow)) {
        newTimes.add(TimeRange.fromStartEnd(availableTime.start(), eventWindow.start(), false));
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), availableTime.end(), false));
        continue;
      } 
      // If eventWindow ends after availableTime starts, push back availableTime's start until eventWindow ends.
      if (availableTime.start() > eventWindow.start() && availableTime.end() > eventWindow.end()) {
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), availableTime.end(), false));
        continue;
      }
      // If eventWindow starts before availableTime ends, move availableTime's end forward.
      if (availableTime.start() > eventWindow.start() && eventWindow.start() > availableTime.end()) {
        newTimes.add(TimeRange.fromStartEnd(availableTime.start(), eventWindow.start(), false));
        continue;
      } 
    }
    return newTimes;
  }
}
