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

public final class FindMeetingQuery {
  
  /** 
   * Returns the open windows for which {@code request} can be scheduled in.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> requiredAttendees = new ArrayList<>(request.getAttendees());
    Collection<String> allAttendees = new ArrayList<>(requiredAttendees);
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    
    allAttendees.addAll(optionalAttendees);
    
    Collection<TimeRange> availableTimes = assembleTime(events, allAttendees, request.getDuration());
    if (requiredAttendees.isEmpty() || !availableTimes.isEmpty()){
      return availableTimes;
    }
    return assembleTime(events, requiredAttendees, request.getDuration());
  }

  /**
   * Returns the open windows for which {@code request} can be scheduled in,
   * after removing all conflicting time from {@code events}.
   */
  public Collection<TimeRange> assembleTime(Collection<Event> events, Collection<String> requiredAttendees, long duration) {
    Collection<TimeRange> availableTimes = new ArrayList<>();

    // Initally, entire day is open to schedule a meeting in.
    availableTimes.add(TimeRange.WHOLE_DAY);

    // For every event that a requiredAttendee must attended, remove that time from avaibleTimes.
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
    for(TimeRange thisTime : availableTimes) {
      // If timeTime doesn't overlap with eventWindow then thisTime is available.
      if(!thisTime.overlaps(eventWindow)) {
        newTimes.add(thisTime);
        continue;
      } 
      // If eventWindow fits entirely into thisTime then split thisTime into two windows.
      if (thisTime.contains(eventWindow)) {
        newTimes.add(TimeRange.fromStartEnd(thisTime.start(), eventWindow.start(), false));
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), thisTime.end(), false));
        continue;
      } 
      // If eventWindow ends after thisTime starts, push back thisTime's start until eventWindow ends.
      if (thisTime.start() > eventWindow.start() && thisTime.end() > eventWindow.end()) {
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), thisTime.end(), false));
        continue;
      }
      // If eventWindow starts before thisTime ends, move thisTime's end forward.
      if (thisTime.start() > eventWindow.start() && eventWindow.start() > thisTime.end()) {
        newTimes.add(TimeRange.fromStartEnd(thisTime.start(), eventWindow.start(), false));
        continue;
      } 
    }
    return newTimes;
  }
}
