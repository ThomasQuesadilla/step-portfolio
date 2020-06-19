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

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/*
* use later start time and earlier end time
* There are 5 cases between optional and mandatory available times if mand.overlaps(opt) == true
* Mand: |--A--|  |----A----|              |----A----|  |----A----|     |--A--|
* Opt:  |--B--|       |----B----|   |----B----|         |--B--|     |----B----|
*        equals  overhang left      overhang right   mand over opt  opt over mand
*                w/ enough time     w/ enough time
*        returns    returns             returns         returns       returns
*         mand    intersection        intersection        opt           mand
*/                
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long requestDuration = request.getDuration();
    ArrayList<TimeRange> mandatoryEventTimes = listOfEventRanges(request.getAttendees(), events);
    Collections.sort(mandatoryEventTimes, TimeRange.ORDER_BY_START);
    ArrayList<TimeRange> availableMandatoryTimes = findAvailable(mandatoryEventTimes, requestDuration);
  
    ArrayList<TimeRange> optionalEventTimes = listOfEventRanges(request.getOptionalAttendees(), events);
    Collections.sort(optionalEventTimes, TimeRange.ORDER_BY_START);
    ArrayList<TimeRange> availableOptionalTimes = findAvailable(optionalEventTimes, requestDuration);
      
    if (request.getOptionalAttendees().size() == 0 ) {
      return availableMandatoryTimes;
    } else if (request.getAttendees().size() == 0) {
      return availableOptionalTimes;
    } else {
      Collection<TimeRange> resultingTimes = new ArrayList<>();

      for (TimeRange mandatoryRange: availableMandatoryTimes) {
        for (TimeRange optionalRange: availableOptionalTimes) {         
          int rangeStart = (mandatoryRange.start() > optionalRange.start()) ? mandatoryRange.start() : optionalRange.start();
          int rangeEnd = (mandatoryRange.end() < optionalRange.end()) ? mandatoryRange.end() : optionalRange.end();
          
          if (rangeEnd - rangeStart > request.getDuration()) {
            resultingTimes.add(TimeRange.fromStartEnd(rangeStart, rangeEnd, false));
            break;
          }
        }
      }
      if (resultingTimes.size() == 0) {
        return availableMandatoryTimes;
      }
      return resultingTimes;
    }
  }
    
    private ArrayList<TimeRange> listOfEventRanges(Collection<String> requestAttendees, Collection<Event> events) {
      ArrayList<TimeRange> eventTimes = new ArrayList<>();
      for (Event event: events) {
        Set<String> intersection = new HashSet<>(requestAttendees);
        intersection.retainAll(event.getAttendees());
        if (intersection.size() > 0) {
          eventTimes.add(event.getWhen());
        }
      }
      return eventTimes;
    }


  private ArrayList<TimeRange> findAvailable(Collection<TimeRange> eventTimes, long requestDuration) {
    ArrayList<TimeRange> availableTimes = new ArrayList<>();
    TimeRange pointerRange = TimeRange.WHOLE_DAY;
    for (TimeRange timeRange: eventTimes) {
      if (pointerRange.overlaps(timeRange)) {
        if (timeRange.start() - pointerRange.start() >= requestDuration) {
          availableTimes.add(TimeRange.fromStartEnd(pointerRange.start(), timeRange.start(), false));
        }
        pointerRange = TimeRange.fromStartEnd(timeRange.end(), TimeRange.END_OF_DAY, true);
      }
    }
    if (pointerRange.duration() >=  requestDuration) {
      availableTimes.add(pointerRange);
    }
    return availableTimes;
  }
}
