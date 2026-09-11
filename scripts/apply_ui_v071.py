from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parents[1]

def replace(path, old, new):
    p = ROOT / path
    s = p.read_text(encoding='utf-8')
    if old not in s:
        return False
    p.write_text(s.replace(old, new, 1), encoding='utf-8')
    return True

changed = False
changed |= replace(
    'app/src/main/java/tr/yurdunubil/app/ModernMainV4.kt',
    '0 -> HomeV4(p, prefs, { launchQuiz("Hızlı 10", SharedGameModes.quick) }) { arenaOpen = true }',
    '0 -> HomeModernV5(p.bg, p.card, p.text, p.muted, p.green, p.gold, prefs, { launchQuiz("Hızlı 10", SharedGameModes.quick) }, { arenaOpen = true }, { context.startActivity(Intent(context, SocialModernActivity::class.java)) })',
)
# If the previous UI patch already changed the home call, add the notification callback to it.
changed |= replace(
    'app/src/main/java/tr/yurdunubil/app/ModernMainV4.kt',
    '0 -> HomeModernV5(p.bg, p.card, p.text, p.muted, p.green, p.gold, prefs, { launchQuiz("Hızlı 10", SharedGameModes.quick) }) { arenaOpen = true }',
    '0 -> HomeModernV5(p.bg, p.card, p.text, p.muted, p.green, p.gold, prefs, { launchQuiz("Hızlı 10", SharedGameModes.quick) }, { arenaOpen = true }, { context.startActivity(Intent(context, SocialModernActivity::class.java)) })',
)
changed |= replace('app/src/main/java/tr/yurdunubil/app/AdminFloatingEntry.kt', 'Intent(context, AdminCenterActivity::class.java)', 'Intent(context, AdminModernActivity::class.java)')
changed |= replace('app/src/main/java/tr/yurdunubil/app/SocialFloatingEntry.kt', 'Intent(context, SocialCenterActivity::class.java)', 'Intent(context, SocialModernActivity::class.java)')
changed |= replace('app/src/main/AndroidManifest.xml', '        <activity android:name=".SocialCenterActivity" android:exported="false" />', '        <activity android:name=".SocialCenterActivity" android:exported="false" />\n        <activity android:name=".SocialModernActivity" android:exported="false" />')
changed |= replace('app/src/main/AndroidManifest.xml', '        <activity android:name=".AdminCenterActivity" android:exported="false" />', '        <activity android:name=".AdminCenterActivity" android:exported="false" />\n        <activity android:name=".AdminModernActivity" android:exported="false" />')

p = ROOT / 'app/build.gradle.kts'
s = p.read_text(encoding='utf-8')
s2 = s.replace('versionCode = 14', 'versionCode = 15', 1).replace('versionName = "0.7.1"', 'versionName = "0.7.2"', 1)
if s2 != s:
    p.write_text(s2, encoding='utf-8')
    changed = True

if changed:
    subprocess.run(['git', 'config', 'user.name', 'Yurdunu Bil CI'], cwd=ROOT, check=True)
    subprocess.run(['git', 'config', 'user.email', 'actions@yurdunubil.app'], cwd=ROOT, check=True)
    subprocess.run(['git', 'add', 'app', 'scripts'], cwd=ROOT, check=True)
    subprocess.run(['git', 'commit', '-m', 'feat: ship compact modern Yurdunu Bil UI'], cwd=ROOT, check=True)
    subprocess.run(['git', 'push'], cwd=ROOT, check=True)
    print('Modern UI committed to main.')
else:
    print('Modern UI patch already applied.')
