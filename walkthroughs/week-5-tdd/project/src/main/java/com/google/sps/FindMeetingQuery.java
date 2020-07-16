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
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> required = request.getAttendees();
    Collection<TimeRange> availableTime = assembleTime(events, required, request.getDuration());
    return availableTime;
  }

  public Collection<TimeRange> assembleTime(Collection<Event> events, Collection<String> required, long duration) {
    Collection<TimeRange> availableTime = new ArrayList<>();
    availableTime.add(TimeRange.WHOLE_DAY);
    for(Event currEvent : events) {
      if (!Collections.disjoint(currEvent.getAttendees(), required)) {
        availableTime = adjustAvailableTime(availableTime, currEvent.getWhen());
      }
    }

    Iterator<TimeRange> i = availableTime.iterator();
 
    while(i.hasNext()) {
      TimeRange e = i.next();
      if (e.duration() < duration) {
        i.remove();
      }
    }

    return availableTime;
  }

  public Collection<TimeRange> adjustAvailableTime(Collection<TimeRange> availableTime, TimeRange eventWindow) {
    Collection<TimeRange> newTimes = new ArrayList<>();
    for(TimeRange thisTime : availableTime) {
      if(!thisTime.overlaps(eventWindow)) {
        newTimes.add(thisTime);
      } else if (thisTime.contains(eventWindow)) {
        newTimes.add(TimeRange.fromStartEnd(thisTime.start(), eventWindow.start(), false));
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), thisTime.end(), false));
      } else if (thisTime.start() > eventWindow.start() && thisTime.end() > eventWindow.end()) {
        newTimes.add(TimeRange.fromStartEnd(eventWindow.end(), thisTime.end(), false));
      } else if (thisTime.start() > eventWindow.start() && eventWindow.start() > thisTime.end()) {
        newTimes.add(TimeRange.fromStartEnd(thisTime.start(), eventWindow.start(), false));
      } 
    }
    return newTimes;
  }
}
