/*
 * Copyright 2022 Ally Financial, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ally.d3.watchmen.utilities;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskingPatternLayout  extends PatternLayout {

    private String patternsProperty;
    private Optional<Pattern> pattern;

    public String getPatternsProperty() {
        return patternsProperty;
    }

    public void setPatternsProperty(String patternsProperty) {
        this.patternsProperty = patternsProperty;
        if (this.patternsProperty != null) {
            this.pattern = Optional.of(Pattern.compile(patternsProperty, Pattern.MULTILINE));
        } else {
            this.pattern = Optional.empty();
        }
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        final StringBuilder message = new StringBuilder(super.doLayout(event));

        if (pattern.isPresent()) {
            Matcher matcher = pattern.get().matcher(message);
            while (matcher.find()) {

                int group = 1;
                while (group <= matcher.groupCount()) {
                    if (matcher.group(group) != null) {
                        final int startGrpIndex = matcher.start(group);
                        final int endGrpIndex = matcher.end(group);
                        final int diff = endGrpIndex - startGrpIndex + 1;
                        int startIndex = startGrpIndex + diff;
                        final int endIndex1 = message.indexOf(",", startIndex);
                        final int endIndex2 = message.indexOf(" ", startIndex);
                        final int endIndex3 = message.indexOf(")", startIndex);
                        final int endIndex4 = message.indexOf("\n", startIndex);

                        final Integer endIndex = getSmallestInt(
                                Arrays.asList(Integer.valueOf(endIndex1), Integer.valueOf(endIndex2), Integer.valueOf(endIndex3), Integer.valueOf(endIndex4)));
                        if (endIndex == null || endIndex <= 0) {
                            continue;
                        }

                        for (int i = startIndex; i < endIndex; i++) {
                            message.setCharAt(i, '*');
                        }
                    }
                    group++;
                }
            }
        }
        return message.toString();
    }

    private Integer getSmallestInt(List<Integer> integerList) {

        return integerList.stream().filter(integer -> integer > 0).reduce((x, y) -> x < y ? x : y).get();
    }

}