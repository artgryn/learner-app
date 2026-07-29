/* @ds-bundle: {"format":4,"namespace":"RozymahaDesignSystem_417869","components":[{"name":"Button","sourcePath":"components/core/Button.jsx"},{"name":"Card","sourcePath":"components/core/Card.jsx"},{"name":"IconButton","sourcePath":"components/core/IconButton.jsx"},{"name":"ListRow","sourcePath":"components/core/ListRow.jsx"},{"name":"Switch","sourcePath":"components/core/Switch.jsx"},{"name":"Tag","sourcePath":"components/core/Tag.jsx"},{"name":"ProgressStat","sourcePath":"components/data/ProgressStat.jsx"},{"name":"TextField","sourcePath":"components/forms/TextField.jsx"},{"name":"NavBar","sourcePath":"components/navigation/NavBar.jsx"},{"name":"SegmentedControl","sourcePath":"components/navigation/SegmentedControl.jsx"},{"name":"TabBar","sourcePath":"components/navigation/TabBar.jsx"}],"sourceHashes":{"components/core/Button.jsx":"ae915da4fbf5","components/core/Card.jsx":"7404ad119fde","components/core/IconButton.jsx":"038c30d5e7f4","components/core/ListRow.jsx":"aa9cb125c413","components/core/Switch.jsx":"71e0c85c2306","components/core/Tag.jsx":"f24a50d0f124","components/data/ProgressStat.jsx":"3d3a6b1b7e1d","components/forms/TextField.jsx":"c33e2d43310c","components/navigation/NavBar.jsx":"032db932e398","components/navigation/SegmentedControl.jsx":"53211f60b359","components/navigation/TabBar.jsx":"24220405f2ec","ui_kits/ios_app/HomeScreen.jsx":"6f7ddd0e764b","ui_kits/ios_app/ListsScreen.jsx":"45afa3f107b9","ui_kits/ios_app/ReviewScreen.jsx":"55a078baa17d","ui_kits/ios_app/SettingsScreen.jsx":"538bad73be66"},"inlinedExternals":[],"unexposedExports":[]} */

(() => {

const __ds_ns = (window.RozymahaDesignSystem_417869 = window.RozymahaDesignSystem_417869 || {});

const __ds_scope = {};

(__ds_ns.__errors = __ds_ns.__errors || []);

// components/core/Button.jsx
try { (() => {
function Button({
  children,
  variant = "primary",
  size = "medium",
  disabled = false,
  onClick,
  icon,
  style
}) {
  const sizes = {
    small: {
      padding: "6px 14px",
      fontSize: "var(--text-footnote)"
    },
    medium: {
      padding: "11px 20px",
      fontSize: "var(--text-body)"
    },
    large: {
      padding: "15px 24px",
      fontSize: "var(--text-body)"
    }
  };
  const variants = {
    primary: {
      background: disabled ? "var(--color-ink-150)" : "var(--accent-primary)",
      color: disabled ? "var(--text-placeholder)" : "var(--text-on-accent)",
      border: "1px solid transparent"
    },
    secondary: {
      background: "var(--bg-surface)",
      color: disabled ? "var(--text-placeholder)" : "var(--text-primary)",
      border: "1px solid var(--border-hairline)"
    },
    ghost: {
      background: "transparent",
      color: disabled ? "var(--text-placeholder)" : "var(--accent-primary)",
      border: "1px solid transparent"
    },
    destructive: {
      background: disabled ? "var(--color-ink-150)" : "var(--state-error)",
      color: disabled ? "var(--text-placeholder)" : "#fff",
      border: "1px solid transparent"
    }
  };
  return /*#__PURE__*/React.createElement("button", {
    onClick: disabled ? undefined : onClick,
    disabled: disabled,
    style: {
      display: "inline-flex",
      alignItems: "center",
      justifyContent: "center",
      gap: "6px",
      fontFamily: "var(--font-ui)",
      fontWeight: "var(--weight-semibold)",
      borderRadius: "var(--radius-md)",
      cursor: disabled ? "default" : "pointer",
      transition: "opacity var(--duration-fast) var(--ease-standard), transform var(--duration-fast) var(--ease-standard)",
      ...sizes[size],
      ...variants[variant],
      ...style
    },
    onMouseDown: e => {
      if (!disabled) e.currentTarget.style.opacity = "0.75";
    },
    onMouseUp: e => {
      e.currentTarget.style.opacity = "1";
    },
    onMouseLeave: e => {
      e.currentTarget.style.opacity = "1";
    }
  }, icon, children);
}
Object.assign(__ds_scope, { Button });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Button.jsx", error: String((e && e.message) || e) }); }

// components/core/Card.jsx
try { (() => {
function Card({
  children,
  padding = "16px",
  onClick
}) {
  return /*#__PURE__*/React.createElement("div", {
    onClick: onClick,
    style: {
      background: "var(--bg-surface)",
      border: "1px solid var(--border-hairline)",
      borderRadius: "var(--radius-md)",
      boxShadow: "var(--shadow-card)",
      padding,
      cursor: onClick ? "pointer" : "default"
    }
  }, children);
}
Object.assign(__ds_scope, { Card });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Card.jsx", error: String((e && e.message) || e) }); }

// components/core/IconButton.jsx
try { (() => {
function IconButton({
  icon,
  size = 36,
  variant = "ghost",
  onClick,
  "aria-label": ariaLabel
}) {
  const variants = {
    ghost: {
      background: "transparent",
      color: "var(--text-primary)"
    },
    tinted: {
      background: "var(--accent-primary-tint)",
      color: "var(--accent-primary-press)"
    },
    filled: {
      background: "var(--accent-primary)",
      color: "#fff"
    }
  };
  return /*#__PURE__*/React.createElement("button", {
    onClick: onClick,
    "aria-label": ariaLabel,
    style: {
      width: size,
      height: size,
      borderRadius: "var(--radius-pill)",
      border: "none",
      display: "inline-flex",
      alignItems: "center",
      justifyContent: "center",
      cursor: "pointer",
      transition: "opacity var(--duration-fast) var(--ease-standard)",
      ...variants[variant]
    },
    onMouseDown: e => {
      e.currentTarget.style.opacity = "0.7";
    },
    onMouseUp: e => {
      e.currentTarget.style.opacity = "1";
    },
    onMouseLeave: e => {
      e.currentTarget.style.opacity = "1";
    }
  }, icon);
}
Object.assign(__ds_scope, { IconButton });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/IconButton.jsx", error: String((e && e.message) || e) }); }

// components/core/ListRow.jsx
try { (() => {
function ListRow({
  title,
  subtitle,
  trailing,
  onClick,
  style
}) {
  return /*#__PURE__*/React.createElement("div", {
    onClick: onClick,
    style: {
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
      padding: "12px 4px",
      borderBottom: "1px solid var(--border-hairline)",
      cursor: onClick ? "pointer" : "default",
      fontFamily: "var(--font-ui)",
      ...style
    }
  }, /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-body)",
      color: "var(--text-primary)"
    }
  }, title), subtitle && /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-footnote)",
      color: "var(--text-secondary)",
      marginTop: "2px"
    }
  }, subtitle)), /*#__PURE__*/React.createElement("div", null, trailing));
}
Object.assign(__ds_scope, { ListRow });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/ListRow.jsx", error: String((e && e.message) || e) }); }

// components/core/Switch.jsx
try { (() => {
function Switch({
  checked = false,
  onChange,
  disabled = false
}) {
  return /*#__PURE__*/React.createElement("button", {
    role: "switch",
    "aria-checked": checked,
    disabled: disabled,
    onClick: () => onChange && onChange(!checked),
    style: {
      width: 51,
      height: 31,
      borderRadius: "var(--radius-pill)",
      border: "none",
      padding: 2,
      background: disabled ? "var(--color-ink-150)" : checked ? "var(--state-success)" : "var(--color-ink-150)",
      display: "flex",
      justifyContent: checked ? "flex-end" : "flex-start",
      cursor: disabled ? "default" : "pointer",
      transition: "background var(--duration-base) var(--ease-standard)"
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      width: 27,
      height: 27,
      borderRadius: "50%",
      background: "#fff",
      boxShadow: "0 1px 3px rgba(0,0,0,0.25)",
      transition: "transform var(--duration-base) var(--ease-standard)"
    }
  }));
}
Object.assign(__ds_scope, { Switch });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Switch.jsx", error: String((e && e.message) || e) }); }

// components/core/Tag.jsx
try { (() => {
function Tag({
  children,
  tone = "sand"
}) {
  const tones = {
    sand: {
      background: "var(--bg-tag)",
      color: "var(--color-ink-700)"
    },
    success: {
      background: "var(--state-success-tint)",
      color: "var(--color-forest-700)"
    },
    error: {
      background: "var(--state-error-tint)",
      color: "var(--color-brick-700)"
    },
    highlight: {
      background: "var(--state-highlight-tint)",
      color: "var(--color-ink-900)"
    },
    accent: {
      background: "var(--accent-primary-tint)",
      color: "var(--accent-primary-press)"
    }
  };
  return /*#__PURE__*/React.createElement("span", {
    style: {
      display: "inline-flex",
      alignItems: "center",
      fontFamily: "var(--font-ui)",
      fontSize: "var(--text-caption1)",
      fontWeight: "var(--weight-medium)",
      padding: "3px 10px",
      borderRadius: "var(--radius-pill)",
      ...tones[tone]
    }
  }, children);
}
Object.assign(__ds_scope, { Tag });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Tag.jsx", error: String((e && e.message) || e) }); }

// components/data/ProgressStat.jsx
try { (() => {
function ProgressStat({
  value,
  label,
  mono = true
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      fontFamily: "var(--font-ui)"
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontFamily: mono ? "var(--font-mono)" : "var(--font-ui)",
      fontSize: "28px",
      fontWeight: 600,
      color: "var(--text-primary)"
    }
  }, value), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-caption1)",
      color: "var(--text-secondary)"
    }
  }, label));
}
Object.assign(__ds_scope, { ProgressStat });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/data/ProgressStat.jsx", error: String((e && e.message) || e) }); }

// components/forms/TextField.jsx
try { (() => {
function TextField({
  value,
  onChange,
  placeholder,
  label,
  multiline = false
}) {
  const Tag = multiline ? "textarea" : "input";
  return /*#__PURE__*/React.createElement("label", {
    style: {
      display: "block",
      fontFamily: "var(--font-ui)"
    }
  }, label && /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-footnote)",
      color: "var(--text-secondary)",
      marginBottom: "6px"
    }
  }, label), /*#__PURE__*/React.createElement(Tag, {
    value: value,
    onChange: e => onChange && onChange(e.target.value),
    placeholder: placeholder,
    rows: multiline ? 3 : undefined,
    style: {
      width: "100%",
      fontFamily: "var(--font-ui)",
      fontSize: "var(--text-body)",
      color: "var(--text-primary)",
      background: "var(--bg-surface-sunk)",
      border: "none",
      borderRadius: "var(--radius-md)",
      padding: "12px 14px",
      outline: "none",
      resize: multiline ? "vertical" : "none"
    }
  }));
}
Object.assign(__ds_scope, { TextField });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/TextField.jsx", error: String((e && e.message) || e) }); }

// components/navigation/NavBar.jsx
try { (() => {
function NavBar({
  title,
  leading,
  trailing
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
      height: 44,
      padding: "0 8px",
      fontFamily: "var(--font-ui)",
      background: "var(--bg-app)",
      borderBottom: "1px solid var(--border-hairline-on-paper)"
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: 60,
      display: "flex"
    }
  }, leading), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-headline)",
      fontWeight: "var(--weight-semibold)",
      color: "var(--text-primary)"
    }
  }, title), /*#__PURE__*/React.createElement("div", {
    style: {
      width: 60,
      display: "flex",
      justifyContent: "flex-end"
    }
  }, trailing));
}
Object.assign(__ds_scope, { NavBar });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/NavBar.jsx", error: String((e && e.message) || e) }); }

// components/navigation/SegmentedControl.jsx
try { (() => {
function SegmentedControl({
  options,
  value,
  onChange
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: "inline-flex",
      background: "var(--bg-surface-sunk)",
      borderRadius: "var(--radius-md)",
      padding: "2px",
      fontFamily: "var(--font-ui)"
    }
  }, options.map(opt => {
    const active = opt.value === value;
    return /*#__PURE__*/React.createElement("button", {
      key: opt.value,
      onClick: () => onChange && onChange(opt.value),
      style: {
        padding: "6px 16px",
        fontSize: "var(--text-footnote)",
        fontWeight: "var(--weight-medium)",
        border: "none",
        borderRadius: "calc(var(--radius-md) - 2px)",
        background: active ? "var(--bg-surface)" : "transparent",
        color: active ? "var(--text-primary)" : "var(--text-secondary)",
        boxShadow: active ? "var(--shadow-card)" : "none",
        cursor: "pointer",
        transition: "all var(--duration-fast) var(--ease-standard)"
      }
    }, opt.label);
  }));
}
Object.assign(__ds_scope, { SegmentedControl });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/SegmentedControl.jsx", error: String((e && e.message) || e) }); }

// components/navigation/TabBar.jsx
try { (() => {
function TabBar({
  items,
  value,
  onChange
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: "flex",
      borderTop: "1px solid var(--border-hairline-on-paper)",
      background: "var(--bg-app)",
      padding: "6px 0 10px"
    }
  }, items.map(item => {
    const active = item.value === value;
    return /*#__PURE__*/React.createElement("button", {
      key: item.value,
      onClick: () => onChange && onChange(item.value),
      style: {
        flex: 1,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: "2px",
        border: "none",
        background: "transparent",
        cursor: "pointer",
        color: active ? "var(--accent-primary)" : "var(--text-secondary)"
      }
    }, /*#__PURE__*/React.createElement("div", {
      style: {
        width: 24,
        height: 24,
        display: "flex",
        alignItems: "center",
        justifyContent: "center"
      }
    }, item.icon), /*#__PURE__*/React.createElement("div", {
      style: {
        fontFamily: "var(--font-ui)",
        fontSize: "var(--text-caption2)",
        fontWeight: "var(--weight-medium)"
      }
    }, item.label));
  }));
}
Object.assign(__ds_scope, { TabBar });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/TabBar.jsx", error: String((e && e.message) || e) }); }

// ui_kits/ios_app/HomeScreen.jsx
try { (() => {
const Icon = ({
  name,
  size = 18,
  color
}) => /*#__PURE__*/React.createElement("img", {
  src: `https://unpkg.com/lucide-static@latest/icons/${name}.svg`,
  width: size,
  height: size,
  style: {
    display: "block"
  }
});
function HomeScreen({
  NavBar,
  Card,
  ProgressStat,
  TabBar,
  IconButton,
  tab,
  setTab
}) {
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(NavBar, {
    title: "Home",
    trailing: /*#__PURE__*/React.createElement(IconButton, {
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "settings"
      }),
      variant: "ghost",
      "aria-label": "Settings"
    })
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      padding: 20,
      display: "flex",
      flexDirection: "column",
      gap: 20,
      flex: 1
    }
  }, /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-largetitle)",
      fontWeight: 700
    }
  }, "Good evening"), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-subhead)",
      color: "var(--text-secondary)",
      marginTop: 2
    }
  }, "12 words due for review")), /*#__PURE__*/React.createElement(Card, {
    padding: "20px"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: "flex",
      justifyContent: "space-between"
    }
  }, /*#__PURE__*/React.createElement(ProgressStat, {
    value: "3,482",
    label: "words known"
  }), /*#__PURE__*/React.createElement(ProgressStat, {
    value: "86%",
    label: "retention"
  }), /*#__PURE__*/React.createElement(ProgressStat, {
    value: "21",
    label: "day streak"
  }))), /*#__PURE__*/React.createElement(Card, {
    padding: "0"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      padding: "16px 16px 0"
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-headline)",
      fontWeight: 600
    }
  }, "Retention, last 30 days")), /*#__PURE__*/React.createElement("svg", {
    viewBox: "0 0 320 90",
    width: "100%",
    height: "90",
    style: {
      display: "block"
    }
  }, /*#__PURE__*/React.createElement("polyline", {
    points: "0,70 40,60 80,62 120,45 160,48 200,32 240,30 280,18 320,14",
    fill: "none",
    stroke: "var(--chart-line)",
    strokeWidth: "2.5"
  }), /*#__PURE__*/React.createElement("polygon", {
    points: "0,70 40,60 80,62 120,45 160,48 200,32 240,30 280,18 320,14 320,90 0,90",
    fill: "var(--chart-fill)",
    opacity: "0.5"
  }))), /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-headline)",
      fontWeight: 600,
      marginBottom: 8
    }
  }, "Goal progress"), /*#__PURE__*/React.createElement(Card, {
    padding: "16px"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: "flex",
      justifyContent: "space-between",
      fontSize: "var(--text-footnote)",
      color: "var(--text-secondary)",
      marginBottom: 6
    }
  }, /*#__PURE__*/React.createElement("span", null, "Core 3,000 words"), /*#__PURE__*/React.createElement("span", null, "3,482 / 5,000")), /*#__PURE__*/React.createElement("div", {
    style: {
      height: 8,
      background: "var(--bg-surface-sunk)",
      borderRadius: 999
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: "70%",
      height: 8,
      background: "var(--accent-primary)",
      borderRadius: 999
    }
  })))), /*#__PURE__*/React.createElement("button", {
    style: {
      marginTop: "auto",
      background: "var(--accent-primary)",
      color: "#fff",
      border: "none",
      borderRadius: "var(--radius-md)",
      padding: "15px",
      fontFamily: "var(--font-ui)",
      fontSize: "var(--text-body)",
      fontWeight: 600,
      cursor: "pointer"
    },
    onClick: () => setTab("review")
  }, "Start today's review")), /*#__PURE__*/React.createElement(TabBar, {
    value: tab,
    onChange: setTab,
    items: [{
      label: "Home",
      value: "home",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "house"
      })
    }, {
      label: "Lists",
      value: "lists",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "list"
      })
    }, {
      label: "Review",
      value: "review",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "layers"
      })
    }, {
      label: "Settings",
      value: "settings",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "settings"
      })
    }]
  }));
}
window.RozymahaUiKit = Object.assign(window.RozymahaUiKit || {}, {
  Icon,
  HomeScreen
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/ios_app/HomeScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/ios_app/ListsScreen.jsx
try { (() => {
function ListsScreen({
  NavBar,
  Card,
  ListRow,
  Tag,
  SegmentedControl,
  TabBar,
  IconButton,
  tab,
  setTab,
  onNewList
}) {
  const Icon = window.RozymahaUiKit.Icon;
  const [seg, setSeg] = React.useState("all");
  const lists = [{
    title: "Travel phrases",
    subtitle: "120 words · 68% known",
    level: "A2",
    tone: "accent"
  }, {
    title: "Kitchen & food",
    subtitle: "84 words · 41% known",
    level: "A1",
    tone: "sand"
  }, {
    title: "Business meetings",
    subtitle: "200 words · 12% known",
    level: "B1",
    tone: "highlight"
  }, {
    title: "Weather & seasons",
    subtitle: "45 words · 90% known",
    level: "A1",
    tone: "sand"
  }, {
    title: "Emotions & feelings",
    subtitle: "60 words · 55% known",
    level: "A2",
    tone: "accent"
  }];
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(NavBar, {
    title: "Vocab lists",
    trailing: /*#__PURE__*/React.createElement(IconButton, {
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "plus"
      }),
      variant: "tinted",
      "aria-label": "New list",
      onClick: onNewList
    })
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      padding: "16px 20px",
      flex: 1,
      display: "flex",
      flexDirection: "column",
      gap: 16
    }
  }, /*#__PURE__*/React.createElement(SegmentedControl, {
    options: [{
      label: "All",
      value: "all"
    }, {
      label: "My lists",
      value: "mine"
    }, {
      label: "Saved",
      value: "saved"
    }],
    value: seg,
    onChange: setSeg
  }), /*#__PURE__*/React.createElement(Card, {
    padding: "0 16px"
  }, lists.map((l, i) => /*#__PURE__*/React.createElement(ListRow, {
    key: l.title,
    title: l.title,
    subtitle: l.subtitle,
    trailing: /*#__PURE__*/React.createElement(Tag, {
      tone: l.tone
    }, l.level),
    onClick: () => {},
    style: i === lists.length - 1 ? {
      borderBottom: "none"
    } : undefined
  })))), /*#__PURE__*/React.createElement(TabBar, {
    value: tab,
    onChange: setTab,
    items: [{
      label: "Home",
      value: "home",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "house"
      })
    }, {
      label: "Lists",
      value: "lists",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "list"
      })
    }, {
      label: "Review",
      value: "review",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "layers"
      })
    }, {
      label: "Settings",
      value: "settings",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "settings"
      })
    }]
  }));
}
function NewListSheet({
  TextField,
  Button,
  onCancel,
  onGenerate
}) {
  const [prompt, setPrompt] = React.useState("");
  const [name, setName] = React.useState("");
  return /*#__PURE__*/React.createElement("div", {
    style: {
      position: "absolute",
      inset: 0,
      background: "rgba(43,38,32,0.35)",
      display: "flex",
      alignItems: "flex-end"
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: "100%",
      background: "var(--bg-surface)",
      borderRadius: "20px 20px 0 0",
      padding: 20,
      boxShadow: "var(--shadow-sheet)",
      display: "flex",
      flexDirection: "column",
      gap: 14
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-title3)",
      fontWeight: 700
    }
  }, "New list"), /*#__PURE__*/React.createElement(TextField, {
    label: "List name",
    value: name,
    onChange: setName,
    placeholder: "Travel phrases"
  }), /*#__PURE__*/React.createElement(TextField, {
    label: "Generate with AI",
    value: prompt,
    onChange: setPrompt,
    placeholder: "20 words for ordering coffee in Italian",
    multiline: true
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      display: "flex",
      gap: 8
    }
  }, /*#__PURE__*/React.createElement(Button, {
    variant: "secondary",
    onClick: onCancel,
    style: {
      flex: 1
    }
  }, "Cancel"), /*#__PURE__*/React.createElement(Button, {
    variant: "primary",
    onClick: onGenerate,
    style: {
      flex: 1
    }
  }, "Generate list"))));
}
window.RozymahaUiKit = Object.assign(window.RozymahaUiKit || {}, {
  ListsScreen,
  NewListSheet
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/ios_app/ListsScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/ios_app/ReviewScreen.jsx
try { (() => {
function ReviewScreen({
  NavBar,
  Button,
  IconButton,
  TabBar,
  tab,
  setTab,
  onExit
}) {
  const Icon = window.RozymahaUiKit.Icon;
  const cards = [{
    word: "manzana",
    meaning: "apple · noun · fem.",
    example: "La manzana está en la mesa."
  }, {
    word: "correr",
    meaning: "to run · verb",
    example: "Me gusta correr por la mañana."
  }, {
    word: "azul",
    meaning: "blue · adjective",
    example: "El cielo es azul."
  }];
  const [i, setI] = React.useState(0);
  const [revealed, setRevealed] = React.useState(false);
  const card = cards[i % cards.length];
  const next = () => {
    setRevealed(false);
    setI(v => v + 1);
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(NavBar, {
    title: `${i % cards.length + 1} of ${cards.length}`,
    leading: /*#__PURE__*/React.createElement(IconButton, {
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "x"
      }),
      variant: "ghost",
      "aria-label": "End session",
      onClick: onExit
    })
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      display: "flex",
      flexDirection: "column",
      padding: 20,
      gap: 20
    }
  }, /*#__PURE__*/React.createElement("div", {
    onClick: () => setRevealed(v => !v),
    style: {
      flex: 1,
      background: "var(--bg-surface)",
      border: "1px solid var(--border-hairline)",
      borderRadius: "var(--radius-lg)",
      boxShadow: "var(--shadow-card)",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      gap: 10,
      cursor: "pointer",
      padding: 24,
      textAlign: "center"
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontFamily: "var(--font-word)",
      fontSize: "var(--text-word-display)"
    }
  }, card.word), revealed ? /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-subhead)",
      color: "var(--text-secondary)"
    }
  }, card.meaning), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-footnote)",
      color: "var(--text-secondary)",
      fontStyle: "italic",
      marginTop: 8
    }
  }, "\u201C", card.example, "\u201D")) : /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-footnote)",
      color: "var(--text-placeholder)"
    }
  }, "Tap to reveal")), revealed ? /*#__PURE__*/React.createElement("div", {
    style: {
      display: "flex",
      gap: 10
    }
  }, /*#__PURE__*/React.createElement(Button, {
    variant: "destructive",
    size: "large",
    onClick: next,
    style: {
      flex: 1
    }
  }, "Again"), /*#__PURE__*/React.createElement(Button, {
    variant: "secondary",
    size: "large",
    onClick: next,
    style: {
      flex: 1
    }
  }, "Good"), /*#__PURE__*/React.createElement(Button, {
    variant: "primary",
    size: "large",
    onClick: next,
    style: {
      flex: 1
    }
  }, "Easy")) : /*#__PURE__*/React.createElement(Button, {
    variant: "primary",
    size: "large",
    onClick: () => setRevealed(true)
  }, "Show answer")), /*#__PURE__*/React.createElement(TabBar, {
    value: tab,
    onChange: setTab,
    items: [{
      label: "Home",
      value: "home",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "house"
      })
    }, {
      label: "Lists",
      value: "lists",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "list"
      })
    }, {
      label: "Review",
      value: "review",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "layers"
      })
    }, {
      label: "Settings",
      value: "settings",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "settings"
      })
    }]
  }));
}
window.RozymahaUiKit = Object.assign(window.RozymahaUiKit || {}, {
  ReviewScreen
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/ios_app/ReviewScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/ios_app/SettingsScreen.jsx
try { (() => {
function SettingsScreen({
  NavBar,
  ListRow,
  Switch,
  TabBar,
  tab,
  setTab
}) {
  const Icon = window.RozymahaUiKit.Icon;
  const [daily, setDaily] = React.useState(true);
  const [sound, setSound] = React.useState(false);
  const [autoAdvance, setAutoAdvance] = React.useState(true);
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(NavBar, {
    title: "Settings"
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      padding: "16px 20px",
      flex: 1,
      display: "flex",
      flexDirection: "column",
      gap: 20
    }
  }, /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-footnote)",
      color: "var(--text-secondary)",
      marginBottom: 6,
      textTransform: "uppercase",
      letterSpacing: "0.03em"
    }
  }, "Daily goal"), /*#__PURE__*/React.createElement(ListRow, {
    title: "Words per day",
    subtitle: "15 words",
    trailing: /*#__PURE__*/React.createElement(Icon, {
      name: "chevron-right"
    }),
    onClick: () => {}
  })), /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-footnote)",
      color: "var(--text-secondary)",
      marginBottom: 6,
      textTransform: "uppercase",
      letterSpacing: "0.03em"
    }
  }, "Review session"), /*#__PURE__*/React.createElement(ListRow, {
    title: "Daily reminder",
    trailing: /*#__PURE__*/React.createElement(Switch, {
      checked: daily,
      onChange: setDaily
    })
  }), /*#__PURE__*/React.createElement(ListRow, {
    title: "Sound effects",
    trailing: /*#__PURE__*/React.createElement(Switch, {
      checked: sound,
      onChange: setSound
    })
  }), /*#__PURE__*/React.createElement(ListRow, {
    title: "Auto-advance after grading",
    trailing: /*#__PURE__*/React.createElement(Switch, {
      checked: autoAdvance,
      onChange: setAutoAdvance
    }),
    style: {
      borderBottom: "none"
    }
  })), /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: "var(--text-footnote)",
      color: "var(--text-secondary)",
      marginBottom: 6,
      textTransform: "uppercase",
      letterSpacing: "0.03em"
    }
  }, "Account"), /*#__PURE__*/React.createElement(ListRow, {
    title: "Export progress",
    trailing: /*#__PURE__*/React.createElement(Icon, {
      name: "chevron-right"
    }),
    onClick: () => {}
  }), /*#__PURE__*/React.createElement(ListRow, {
    title: "Sign out",
    trailing: /*#__PURE__*/React.createElement(Icon, {
      name: "chevron-right"
    }),
    onClick: () => {},
    style: {
      borderBottom: "none"
    }
  }))), /*#__PURE__*/React.createElement(TabBar, {
    value: tab,
    onChange: setTab,
    items: [{
      label: "Home",
      value: "home",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "house"
      })
    }, {
      label: "Lists",
      value: "lists",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "list"
      })
    }, {
      label: "Review",
      value: "review",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "layers"
      })
    }, {
      label: "Settings",
      value: "settings",
      icon: /*#__PURE__*/React.createElement(Icon, {
        name: "settings"
      })
    }]
  }));
}
window.RozymahaUiKit = Object.assign(window.RozymahaUiKit || {}, {
  SettingsScreen
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/ios_app/SettingsScreen.jsx", error: String((e && e.message) || e) }); }

__ds_ns.Button = __ds_scope.Button;

__ds_ns.Card = __ds_scope.Card;

__ds_ns.IconButton = __ds_scope.IconButton;

__ds_ns.ListRow = __ds_scope.ListRow;

__ds_ns.Switch = __ds_scope.Switch;

__ds_ns.Tag = __ds_scope.Tag;

__ds_ns.ProgressStat = __ds_scope.ProgressStat;

__ds_ns.TextField = __ds_scope.TextField;

__ds_ns.NavBar = __ds_scope.NavBar;

__ds_ns.SegmentedControl = __ds_scope.SegmentedControl;

__ds_ns.TabBar = __ds_scope.TabBar;

})();
