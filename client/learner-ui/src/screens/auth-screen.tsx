import { useState } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';

import {
  ApiError,
  confirmPasswordReset,
  login,
  register,
  requestPasswordReset,
} from '@/api';
import { Button } from '@/components/ui/button';
import { Screen } from '@/components/ui/screen';
import { TextField } from '@/components/ui/text-field';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';

type AuthMode = 'login' | 'register' | 'reset';

type AuthScreenProps = {
  /** isNewAccount: true after register (needs init_account), false after login. */
  onAuthenticated: (isNewAccount: boolean) => void;
};

const ERROR_MESSAGES: Record<string, string> = {
  EMAIL_TAKEN: 'An account with this email already exists.',
  INVALID_OR_EXPIRED_CODE: 'That code is invalid or has expired.',
};

function messageFor(error: unknown): string {
  if (error instanceof ApiError) {
    return ERROR_MESSAGES[error.code] ?? error.message;
  }
  return 'Something went wrong. Please try again.';
}

// One view, switched by `mode` — matches doc/app-info/UI/login-register-reset.md.
export function AuthScreen({ onAuthenticated }: AuthScreenProps) {
  const [mode, setMode] = useState<AuthMode>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  function resetTransientFields() {
    setPassword('');
    setConfirmPassword('');
    setCode('');
    setNewPassword('');
    setConfirmNewPassword('');
    setError(null);
    setNotice(null);
  }

  async function handleLogin() {
    setSubmitting(true);
    setError(null);
    try {
      await login({ email, password });
      onAuthenticated(false);
    } catch (err) {
      setError(messageFor(err));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleRegister() {
    if (password !== confirmPassword) {
      setError("Passwords don't match.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      // `code` isn't sent — registration isn't gated on the confirmation email (see doc/CLAUDE.md);
      // the field is shown to match the wireframe but has no confirm-registration endpoint yet.
      await register({ email, password });
      onAuthenticated(true);
    } catch (err) {
      setError(messageFor(err));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleForgotPassword() {
    if (!email) {
      setError('Enter your email first.');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      await requestPasswordReset({ email });
      resetTransientFields();
      setMode('reset');
    } catch (err) {
      setError(messageFor(err));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleResendCode() {
    setSubmitting(true);
    setError(null);
    try {
      await requestPasswordReset({ email });
      setNotice('Code resent.');
    } catch (err) {
      setError(messageFor(err));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleResetPassword() {
    if (newPassword !== confirmNewPassword) {
      setError("Passwords don't match.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      await confirmPasswordReset({ email, code, newPassword });
      resetTransientFields();
      setNotice('Password reset — log in with your new password.');
      setMode('login');
    } catch (err) {
      setError(messageFor(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Screen>
      <View style={styles.content}>
        <ThemedText type="title2" color="accentPrimary">
          Rozymaha
        </ThemedText>

        {mode === 'reset' ? (
          <>
            <ThemedText type="title2">Reset password</ThemedText>
            <ThemedText type="footnote" color="textSecondary">
              Enter the code we emailed you and choose a new password
            </ThemedText>
            <TextField
              label="Confirmation code"
              value={code}
              onChangeText={setCode}
              placeholder="4-digit code"
              keyboardType="number-pad"
            />
            <TextField
              label="New password"
              value={newPassword}
              onChangeText={setNewPassword}
              secureTextEntry
            />
            <TextField
              label="Confirm new password"
              value={confirmNewPassword}
              onChangeText={setConfirmNewPassword}
              secureTextEntry
            />
            <Button variant="primary" disabled={submitting} onPress={handleResetPassword}>
              Reset password
            </Button>
            <Pressable onPress={handleResendCode} disabled={submitting}>
              <ThemedText type="footnote" color="accentPrimary" style={styles.link}>
                Resend code
              </ThemedText>
            </Pressable>
          </>
        ) : mode === 'register' ? (
          <>
            <ThemedText type="title2">Create account</ThemedText>
            <TextField
              label="Email"
              value={email}
              onChangeText={setEmail}
              autoCapitalize="none"
              keyboardType="email-address"
            />
            <TextField label="Password" value={password} onChangeText={setPassword} secureTextEntry />
            <TextField
              label="Confirm password"
              value={confirmPassword}
              onChangeText={setConfirmPassword}
              secureTextEntry
            />
            <TextField
              label="Confirmation code (sent to your email)"
              value={code}
              onChangeText={setCode}
              placeholder="4-digit code"
              keyboardType="number-pad"
            />
            <Button variant="primary" disabled={submitting} onPress={handleRegister}>
              Create account
            </Button>
            <Pressable onPress={() => { resetTransientFields(); setMode('login'); }} disabled={submitting}>
              <ThemedText type="footnote" color="accentPrimary" style={styles.link}>
                Already have an account? Log in
              </ThemedText>
            </Pressable>
          </>
        ) : (
          <>
            <ThemedText type="title2">Log in</ThemedText>
            <TextField
              label="Email"
              value={email}
              onChangeText={setEmail}
              autoCapitalize="none"
              keyboardType="email-address"
            />
            <TextField label="Password" value={password} onChangeText={setPassword} secureTextEntry />
            <Button variant="primary" disabled={submitting} onPress={handleLogin}>
              Log in
            </Button>
            <Pressable onPress={() => { resetTransientFields(); setMode('register'); }} disabled={submitting}>
              <ThemedText type="footnote" color="accentPrimary" style={styles.link}>
                Create an account
              </ThemedText>
            </Pressable>
            <Pressable onPress={handleForgotPassword} disabled={submitting}>
              <ThemedText type="footnote" color="accentPrimary" style={styles.link}>
                Forgot password?
              </ThemedText>
            </Pressable>
          </>
        )}

        {notice ? (
          <ThemedText type="footnote" color="success">
            {notice}
          </ThemedText>
        ) : null}
        {error ? (
          <ThemedText type="footnote" color="error">
            {error}
          </ThemedText>
        ) : null}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    flex: 1,
    justifyContent: 'center',
    gap: Space[4],
  },
  link: {
    textAlign: 'center',
  },
});
