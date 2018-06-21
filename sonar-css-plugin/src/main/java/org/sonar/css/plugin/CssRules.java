/*
 * SonarCSS
 * Copyright (C) 2018-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.css.plugin;

import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.rule.RuleKey;
import org.sonar.css.plugin.rules.ColorNoInvalidHex;
import org.sonar.css.plugin.rules.CommentNoEmpty;
import org.sonar.css.plugin.rules.CssRule;
import org.sonar.css.plugin.rules.UnitNoUnknown;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CssRules {

  private final Map<String, RuleKey> stylelintKeyToRuleKey;
  private final StylelintConfig config = new StylelintConfig();

  public CssRules(CheckFactory checkFactory) {
    Checks<CssRule> checks = checkFactory.<CssRule>create(CssRulesDefinition.REPOSITORY_KEY)
      .addAnnotatedChecks((Iterable) getRuleClasses());
    Collection<CssRule> enabledRules = checks.all();
    stylelintKeyToRuleKey = new HashMap<>();
    for (CssRule rule : enabledRules) {
      stylelintKeyToRuleKey.put(rule.stylelintKey(), checks.ruleKey(rule));
      config.rules.put(rule.stylelintKey(), true);
    }
  }

  public static List<Class> getRuleClasses() {
    return Collections.unmodifiableList(Arrays.asList(
        ColorNoInvalidHex.class,
        UnitNoUnknown.class,
        CommentNoEmpty.class
    ));
  }

  public RuleKey getSonarKey(String stylelintKey) {
    RuleKey ruleKey = stylelintKeyToRuleKey.get(stylelintKey);
    if (ruleKey == null) {
      throw new IllegalStateException("Unknown stylelint rule or rule not enabled " + stylelintKey);
    }
    return ruleKey;
  }

  public StylelintConfig getConfig() {
    return config;
  }

  public static class StylelintConfig {
    Map<String, Boolean> rules = new HashMap<>();
  }
}