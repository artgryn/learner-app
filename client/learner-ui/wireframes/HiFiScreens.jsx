const DS = () => window.RozymahaDesignSystem_417869;
const Icon = ({ name, size = 18, color }) => (
  <img src={`https://unpkg.com/lucide-static@latest/icons/${name}.svg`} width={size} height={size} style={{ display: "block", opacity: color ? 1 : 0.85 }} />
);

function TaskShell({ pct = 20, onExit, children }) {
  const { IconButton } = DS();
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 54 }}>
      <div style={{ display: "flex", alignItems: "center", gap: 12, padding: "0 20px 12px" }}>
        <IconButton icon={<Icon name="x" />} variant="ghost" aria-label="End session" onClick={onExit} />
        <div style={{ flex: 1, height: 4, borderRadius: 999, background: "var(--bg-surface-sunk)" }}>
          <div style={{ width: pct + "%", height: "100%", borderRadius: 999, background: "var(--accent-primary)" }} />
        </div>
      </div>
      <div style={{ padding: "8px 20px 20px", display: "flex", flexDirection: "column", gap: 18, flex: 1 }}>
        {children}
      </div>
    </div>
  );
}

function PromptCard({ eyebrow, children }) {
  const { Card } = DS();
  return (
    <Card padding="28px 20px">
      <div style={{ textAlign: "center", display: "flex", flexDirection: "column", gap: 6 }}>
        <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)" }}>{eyebrow}</div>
        {children}
      </div>
    </Card>
  );
}

function answerStyle(state) {
  if (state === "correct") return { border: "1.5px solid var(--state-success)", background: "var(--state-success-tint)" };
  if (state === "wrong") return { border: "1.5px solid var(--state-error)", background: "var(--state-error-tint)" };
  return { border: "1px solid var(--border-hairline)", background: "var(--bg-surface)" };
}

function AnswerGrid({ options, columns = 2 }) {
  return (
    <div style={{ display: "grid", gridTemplateColumns: `repeat(${columns}, 1fr)`, gap: 10 }}>
      {options.map((o) => (
        <div
          key={o.w}
          style={{
            padding: "16px 10px",
            textAlign: "center",
            borderRadius: "var(--radius-md)",
            fontSize: "var(--text-body)",
            fontFamily: "var(--font-word)",
            ...answerStyle(o.state),
          }}
        >
          {o.w}
        </div>
      ))}
    </div>
  );
}

const masteryRing = (mastered, total) => (
  <div
    style={{
      width: 22,
      height: 22,
      borderRadius: "50%",
      flexShrink: 0,
      background:
        mastered >= total
          ? "var(--color-forest-600, #2f6f4a)"
          : `conic-gradient(var(--accent-primary) ${(360 * mastered) / total}deg, var(--bg-surface-sunk) 0)`,
    }}
  />
);

// ── Home ──────────────────────────────────────────────
function HiFiHome({ tab, setTab }) {
  const { NavBar, Card, Button, ListRow, TabBar, IconButton } = DS();
  const sessions = [
    { n: 1, topic: "Everyday basics" },
    { n: 2, topic: "Everyday basics" },
    { n: 3, topic: "Travel & transport" },
  ];
  const current = 1;
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 54 }}>
      <NavBar title="Home" trailing={<IconButton icon={<Icon name="settings" />} variant="ghost" aria-label="Settings" />} />
      <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 16, flex: 1 }}>
        <Card padding="20px">
          <div style={{ fontSize: "var(--text-title2)", fontWeight: 700 }}>3 sessions ready</div>
          <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)", marginTop: 4 }}>
            Spaced across the day for better retention
          </div>
          <div style={{ display: "flex", gap: 6, marginTop: 14 }}>
            {sessions.map((s) => (
              <div key={s.n} style={{ flex: 1, height: 4, borderRadius: 999, background: s.n <= current ? "var(--accent-primary)" : "var(--bg-surface-sunk)" }} />
            ))}
          </div>
          <Button variant="primary" style={{ marginTop: 14, width: "100%" }}>Start · session 1</Button>
        </Card>
        <Card padding="0 16px">
          {sessions.map((s, i) => (
            <ListRow
              key={s.n}
              title={`${s.n} · ${s.topic}`}
              trailing={
                <div style={{ width: 22, height: 22, borderRadius: "50%", background: s.n < current ? "var(--accent-primary)" : "var(--bg-surface-sunk)", color: s.n <= current ? "#fff" : "var(--text-tertiary)", fontSize: 12, display: "flex", alignItems: "center", justifyContent: "center" }}>
                  {s.n}
                </div>
              }
              style={i === sessions.length - 1 ? { borderBottom: "none" } : undefined}
            />
          ))}
        </Card>
      </div>
      <TabBar
        value={tab}
        onChange={setTab}
        items={[
          { label: "Home", value: "home", icon: <Icon name="house" /> },
          { label: "Lists", value: "lists", icon: <Icon name="list" /> },
          { label: "Settings", value: "settings", icon: <Icon name="settings" /> },
        ]}
      />
    </div>
  );
}

// ── Generate flow ──────────────────────────────────────
function HiFiGeneratePrompt() {
  const { NavBar, TextField, Button, Tag } = DS();
  const [prompt, setPrompt] = React.useState("Swedish for a work trip");
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 54 }}>
      <NavBar title="New list" />
      <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 16, flex: 1 }}>
        <div style={{ fontSize: "var(--text-subhead)", color: "var(--text-secondary)" }}>What do you want to learn?</div>
        <TextField value={prompt} onChange={setPrompt} placeholder='e.g. "Swedish for a work trip"' multiline />
        <Button variant="primary">Generate list</Button>
        <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
          {["Everyday", "Travel", "Food", "Business"].map((t) => (
            <Tag key={t} tone="sand">{t}</Tag>
          ))}
        </div>
        <div style={{ marginTop: "auto", textAlign: "center", fontSize: "var(--text-footnote)", color: "var(--text-secondary)" }}>
          or browse lists other learners made
        </div>
      </div>
    </div>
  );
}

function HiFiGenerating() {
  const { NavBar, Card } = DS();
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 54 }}>
      <NavBar title="New list" />
      <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 12, flex: 1 }}>
        <Card padding="24px">
          <div style={{ textAlign: "center" }}>
            <div style={{ fontSize: "var(--text-headline)", fontWeight: 600 }}>Finding words for</div>
            <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)", marginTop: 4 }}>"Swedish for a work trip"</div>
            <div style={{ display: "flex", justifyContent: "center", gap: 6, marginTop: 16 }}>
              {[0, 1, 2].map((i) => (
                <div key={i} style={{ width: 8, height: 8, borderRadius: "50%", background: i < 2 ? "var(--accent-primary)" : "var(--bg-surface-sunk)" }} />
              ))}
            </div>
          </div>
        </Card>
        {["möte", "deadline", "…"].map((w) => (
          <div key={w} style={{ padding: "12px 16px", borderRadius: "var(--radius-md)", border: "1px dashed var(--border-hairline)", color: "var(--text-tertiary)", fontSize: "var(--text-body)" }}>
            {w}
          </div>
        ))}
      </div>
    </div>
  );
}

function HiFiReview() {
  const { NavBar, Card, ListRow, Button, TextField } = DS();
  const [title, setTitle] = React.useState("Work trip vocabulary");
  const words = [
    { sv: "möte", en: "meeting", mastered: 0, total: 4 },
    { sv: "deadline", en: "deadline", mastered: 0, total: 4 },
    { sv: "konferens", en: "conference", mastered: 0, total: 4 },
  ];
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 54 }}>
      <NavBar title="Review list" />
      <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 14, flex: 1 }}>
        <TextField label="List name" value={title} onChange={setTitle} />
        <Card padding="0 16px">
          {words.map((w, i) => (
            <ListRow
              key={w.sv}
              title={`${w.sv} — ${w.en}`}
              trailing={
                <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                  {masteryRing(w.mastered, w.total)}
                  <span style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)" }}>{w.mastered}/{w.total}</span>
                </div>
              }
              style={i === words.length - 1 ? { borderBottom: "none" } : undefined}
            />
          ))}
        </Card>
        <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)", textAlign: "center" }}>24 words · not started yet</div>
        <Button variant="primary" style={{ marginTop: "auto" }}>Save list</Button>
      </div>
    </div>
  );
}

// ── Onboarding: language pick ──────────────────────────
function HiFiOnboardingLanguage() {
  const { Card, ListRow, Button } = DS();
  const langs = ["Swedish", "Spanish", "French", "Japanese", "German", "Italian"];
  const [sel, setSel] = React.useState("Swedish");
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 74 }}>
      <div style={{ padding: "0 20px" }}>
        <div style={{ fontSize: "var(--text-largetitle)", fontWeight: 700 }}>What are you learning?</div>
        <div style={{ fontSize: "var(--text-subhead)", color: "var(--text-secondary)", marginTop: 4 }}>You can add more languages later</div>
      </div>
      <div style={{ padding: 20, flex: 1, overflow: "auto" }}>
        <Card padding="0 16px">
          {langs.map((l, i) => (
            <ListRow
              key={l}
              title={l}
              onClick={() => setSel(l)}
              trailing={sel === l ? <Icon name="check" size={18} /> : null}
              style={i === langs.length - 1 ? { borderBottom: "none" } : undefined}
            />
          ))}
        </Card>
      </div>
      <div style={{ padding: 20 }}>
        <Button variant="primary" style={{ width: "100%" }}>Continue</Button>
      </div>
    </div>
  );
}

// ── List browse (other learners' lists) ───────────────
function HiFiListBrowse() {
  const { NavBar, Card, ListRow, Tag, SegmentedControl, IconButton } = DS();
  const [seg, setSeg] = React.useState("popular");
  const lists = [
    { title: "Travel & transport", subtitle: "64 words · made by 2,140 learners", tone: "accent" },
    { title: "Everyday basics", subtitle: "120 words · made by 8,910 learners", tone: "sand" },
    { title: "Restaurant & ordering", subtitle: "48 words · made by 1,205 learners", tone: "sand" },
    { title: "Business meetings", subtitle: "90 words · made by 640 learners", tone: "highlight" },
  ];
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 54 }}>
      <NavBar title="Browse lists" leading={<IconButton icon={<Icon name="chevron-left" />} variant="ghost" aria-label="Back" />} />
      <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 16, flex: 1 }}>
        <SegmentedControl options={[{ label: "Popular", value: "popular" }, { label: "New", value: "new" }]} value={seg} onChange={setSeg} />
        <Card padding="0 16px">
          {lists.map((l, i) => (
            <ListRow key={l.title} title={l.title} subtitle={l.subtitle} trailing={<Tag tone={l.tone}>Use</Tag>} style={i === lists.length - 1 ? { borderBottom: "none" } : undefined} />
          ))}
        </Card>
      </div>
    </div>
  );
}

// ── List progress (per-word mastery) ───────────────────
function HiFiListProgress() {
  const { NavBar, Card, ListRow, IconButton } = DS();
  const words = [
    { sv: "leksak", en: "toy", mastered: 3, total: 4 },
    { sv: "äpple", en: "apple", mastered: 1, total: 4 },
    { sv: "bord", en: "table", mastered: 4, total: 4 },
    { sv: "fönster", en: "window", mastered: 2, total: 4 },
    { sv: "stol", en: "chair", mastered: 0, total: 4 },
  ];
  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%", paddingTop: 54 }}>
      <NavBar title="Everyday basics" leading={<IconButton icon={<Icon name="chevron-left" />} variant="ghost" aria-label="Back" />} />
      <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 12, flex: 1 }}>
        <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)" }}>18/120 words mastered</div>
        <Card padding="0 16px">
          {words.map((w, i) => (
            <ListRow
              key={w.sv}
              title={`${w.sv} — ${w.en}`}
              trailing={
                <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                  {masteryRing(w.mastered, w.total)}
                  <span style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)" }}>{w.mastered}/{w.total}</span>
                </div>
              }
              style={i === words.length - 1 ? { borderBottom: "none" } : undefined}
            />
          ))}
        </Card>
      </div>
    </div>
  );
}

// ── Task screens ────────────────────────────────────────
function HiFiFlashcard() {
  const [revealed, setRevealed] = React.useState(true);
  return (
    <TaskShell pct={20}>
      <div onClick={() => setRevealed((v) => !v)} style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", gap: 10, cursor: "pointer", textAlign: "center" }}>
        <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)" }}>noun · neuter</div>
        <div style={{ fontFamily: "var(--font-word)", fontSize: "var(--text-word-display)" }}>äpple</div>
        {revealed ? (
          <React.Fragment>
            <div style={{ fontSize: "var(--text-subhead)", color: "var(--text-secondary)" }}>apple</div>
            <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)", fontStyle: "italic" }}>"Äpplet ligger på bordet."</div>
          </React.Fragment>
        ) : (
          <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-placeholder)" }}>tap to reveal meaning</div>
        )}
      </div>
    </TaskShell>
  );
}

function HiFiMultipleChoice() {
  const options = [{ w: "apple", state: "neutral" }, { w: "pear", state: "neutral" }, { w: "orange", state: "neutral" }, { w: "plum", state: "neutral" }];
  return (
    <TaskShell pct={45}>
      <PromptCard eyebrow="what does this mean?">
        <div style={{ fontFamily: "var(--font-word)", fontSize: 32, marginTop: 2 }}>äpple</div>
      </PromptCard>
      <AnswerGrid options={options} />
      <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-tertiary)", textAlign: "center" }}>Distractors are the same category, not random words</div>
    </TaskShell>
  );
}

function HiFiCollocation() {
  const options = [{ w: "moget", state: "wrong" }, { w: "mogen", state: "correct" }, { w: "mogna", state: "neutral" }];
  return (
    <TaskShell pct={60}>
      <PromptCard eyebrow="choose the word that fits">
        <div style={{ fontFamily: "var(--font-word)", fontSize: 30, marginTop: 2 }}>ett ___ äpple</div>
      </PromptCard>
      <AnswerGrid options={options} columns={3} />
      <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)", textAlign: "center" }}>"mogen" agrees with the neuter noun "äpple"</div>
    </TaskShell>
  );
}

function HiFiNearSynonym() {
  const options = [{ w: "kan", state: "correct" }, { w: "vet", state: "neutral" }];
  return (
    <TaskShell pct={72}>
      <PromptCard eyebrow="which word fits here?">
        <div style={{ fontFamily: "var(--font-word)", fontSize: 24, marginTop: 2 }}>"Jag ___ simma, men det är kallt idag."</div>
      </PromptCard>
      <AnswerGrid options={options} columns={2} />
      <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)", textAlign: "center" }}>kan = know how to · vet = know a fact</div>
    </TaskShell>
  );
}

function HiFiGapFill() {
  const { TextField } = DS();
  const [val] = React.useState("tomater");
  return (
    <TaskShell pct={85}>
      <PromptCard eyebrow="fill in the blank">
        <div style={{ fontFamily: "var(--font-word)", fontSize: 22, marginTop: 2 }}>"Jag köpte två röda ___."</div>
      </PromptCard>
      <TextField value={val} placeholder="type your answer…" />
      <div style={{ display: "flex", alignItems: "center", gap: 6, justifyContent: "center", fontSize: "var(--text-footnote)", color: "var(--color-forest-600, #2f6f4a)" }}>
        <Icon name="check" size={14} /> tomater — correct
      </div>
    </TaskShell>
  );
}

function HiFiFormsTeach() {
  const { Card, Button } = DS();
  const forms = [
    { label: "indef. sg.", sv: "en leksak", en: "a toy" },
    { label: "def. sg.", sv: "leksaken", en: "the toy" },
    { label: "indef. pl.", sv: "leksaker", en: "toys" },
    { label: "def. pl.", sv: "leksakerna", en: "the toys" },
  ];
  return (
    <TaskShell pct={8}>
      <PromptCard eyebrow="noun · common gender — one word, 4 forms">
        <div style={{ fontFamily: "var(--font-word)", fontSize: 36, marginTop: 2 }}>leksak</div>
        <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)" }}>toy</div>
      </PromptCard>
      <Card padding="16px">
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 18 }}>
          {forms.map((f) => (
            <div key={f.label} style={{ textAlign: "center" }}>
              <div style={{ fontSize: "var(--text-caption1)", color: "var(--text-secondary)" }}>{f.label}</div>
              <div style={{ fontSize: "var(--text-subhead)", fontWeight: 600, marginTop: 2, fontFamily: "var(--font-word)" }}>{f.sv}</div>
              <div style={{ fontSize: "var(--text-caption1)", color: "var(--text-secondary)" }}>{f.en}</div>
            </div>
          ))}
        </div>
      </Card>
      <Button variant="primary" style={{ marginTop: "auto" }}>Continue</Button>
    </TaskShell>
  );
}

function HiFiFormsTest() {
  const options = [{ w: "leksak", state: "neutral" }, { w: "leksaken", state: "correct" }, { w: "leksaker", state: "neutral" }, { w: "leksakerna", state: "neutral" }];
  return (
    <TaskShell pct={35}>
      <PromptCard eyebrow="which form fits here?">
        <div style={{ fontFamily: "var(--font-word)", fontSize: 22, marginTop: 2 }}>"Var är ___ du köpte?"</div>
      </PromptCard>
      <AnswerGrid options={options} columns={2} />
      <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-secondary)", textAlign: "center" }}>Same 4 forms taught above — this task rotates through them</div>
    </TaskShell>
  );
}

function HiFiLetterSpell() {
  const answer = "leksak".split("");
  const bank = ["l", "t", "k", "e", "c", "s", "a", "d"];
  const usedCount = { l: 1, e: 1, k: 2, s: 1, a: 1 };
  let seen = {};
  return (
    <TaskShell pct={52}>
      <PromptCard eyebrow="type this word in Swedish">
        <div style={{ fontSize: "var(--text-title2)", fontWeight: 700, marginTop: 2 }}>toy</div>
      </PromptCard>
      <div style={{ display: "flex", gap: 6, justifyContent: "center", flexWrap: "wrap" }}>
        {answer.map((l, i) => (
          <div key={i} style={{ width: 38, height: 44, borderRadius: "var(--radius-md)", border: "1.5px solid var(--accent-primary)", background: "var(--accent-primary-tint)", display: "flex", alignItems: "center", justifyContent: "center", fontFamily: "var(--font-word)", fontSize: 20, fontWeight: 600 }}>
            {l}
          </div>
        ))}
      </div>
      <div style={{ display: "flex", gap: 8, justifyContent: "center", flexWrap: "wrap", marginTop: 8 }}>
        {bank.map((l, i) => {
          seen[l] = (seen[l] || 0) + 1;
          const spent = seen[l] <= (usedCount[l] || 0);
          return (
            <div key={i} style={{ width: 42, height: 48, borderRadius: "var(--radius-md)", border: "1px solid var(--border-hairline)", background: spent ? "var(--bg-surface-sunk)" : "var(--bg-surface)", color: spent ? "var(--text-placeholder)" : "var(--text-primary)", display: "flex", alignItems: "center", justifyContent: "center", fontFamily: "var(--font-word)", fontSize: 20, fontWeight: 600, boxShadow: spent ? "none" : "var(--shadow-card)" }}>
              {l}
            </div>
          );
        })}
      </div>
      <div style={{ fontSize: "var(--text-footnote)", color: "var(--text-tertiary)", textAlign: "center" }}>Tap letters in order — no keyboard, so spelling stays recognition-assisted</div>
    </TaskShell>
  );
}

window.HiFiScreens = {
  HiFiHome, HiFiGeneratePrompt, HiFiGenerating, HiFiReview,
  HiFiOnboardingLanguage, HiFiListBrowse, HiFiListProgress,
  HiFiFlashcard, HiFiMultipleChoice, HiFiCollocation, HiFiNearSynonym, HiFiGapFill, HiFiFormsTeach, HiFiFormsTest, HiFiLetterSpell,
};
