# Git


Git е една от най-популярните version control системи. Ще се опитаме да имплементираме прост API, който симулира някои от по-известните git операции.

### Repository

Хранилището е основна концепция, която пази файлове. През хранилището се осъществяват голяма част от операциите. 
Трябва да бъде възможно създаване на хранилище чрез извикване на default-ния му конструктор - `new Repository()`. 
Позволено е паралелното съществуване на няколко хранилища, които пазят файлове независимо едно от друго.

При създаване на хранилище се създава и branch (разклонение) с име `master`, който е текущият branch, докато не се извика експлицитно `checkoutBranch()` методът.

### Операции

Някои от операциите на хранилището връщат клас `Result`. Той трябва да съдържа следните методи:
- `boolean isSuccessful()` - указва дали операцията е била успешна
- `String getMessage()` - връща съобщението от операцията

`Repository` трябва да поддържа следните операции:

#### Result add(String... files)

Добавя `files` към хранилището. Съобщението от успешна операция е `added {files} to stage`.

```java
Repository repo = new Repository();

Result result = repo.add("foo.txt", "bar.txt", "baz.txt");
result.getMessage(); // "added foo.txt, bar.txt, baz.txt to stage"
```

Когато `add()` е извикан с файл, който вече съществува в хранилището, операцията трябва да бъде неуспешна със съобщение - `'{file}' already exists`. 
Когато `add()` е извикан с повече от един файл и ако някой от файловете вече съществува в хранилището, 
`add()` трябва да върне неуспешна операция за първия вече съществуващ файл, а останалите файлове не трябва да бъдат добавени към хранилището.

```java
Repository repo = new Repository();
repo.add("bar.txt", "baz.txt");

Result result = repo.add("baz.txt");
result.getMessage(); // "'baz.txt' already exists"
```

#### Result commit(String message)

Прави транзакция с файловете, които са добавени/премахнати.

```java
Repository repo = new Repository();
repo.add("foo.txt");
repo.add("bar.txt");

Result result = repo.commit("Initial commit");
result.getMessage(); // "2 files changed"
```

При опит за commit без добавени/премахнати файлове, съобщението от операцията е `nothing to commit, working tree clean` и операцията е неуспешна. 
При успешна операция, съобщението е `{n} files changed`, където `n` е броят на файловете, които са добавени/премахнати.

#### Result remove(String... files)

Премахва `files` от хранилището.

```java
Repository repo = new Repository();
repo.add("foo.txt", "bar.txt", "baz.txt");
repo.commit("Initial commit");

Result result = repo.remove("bar.txt", "baz.txt");
result.getMessage(); // "added bar.txt, baz.txt for removal"
```

Съобщението от успешна операция е `added {files} for removal`. Когато `remove()` е извикан с файл, който не съществува в хранилището, 
операцията трябва да бъде неуспешна със съобщение - `'{file}' did not match any files`. 
Когато `remove()` е извикан с повече от един файл и ако някой от файловете не съществува в хранилището, `remove()` трябва да върне неуспешна операция 
за първия несъществуващ файл, а останалите файлове не трябва да бъдат премахнати от хранилището.

```java
Repository repo = new Repository();
repo.add("foo.txt", "bar.txt");

Result result = repo.remove("foo.txt", "baz.txt");
result.getMessage(); // "'baz.txt' did not match any files"

result = repo.commit("After removal");
result.getMessage(); // "2 files changed"
```

#### Commit getHead()

Връща последния commit в текущия branch. Ако в текущия branch няма никакви commit-и, методът трябва да върне `null`.

`Commit` трябва да съдържа следните методи:
- `String getHash()` - връща хеш на commit-a (в следващите секции е обяснено как се генерира)
- `String getMessage()` - връща съобщението, с което е създаден commit-ът

#### Result log()

Връща историята на commit-ите в текущия branch. При несъщестуващи commit-ити, съобщението за грешка е `branch {name} does not have any commits yet`.

```java
Repository repo = new Repository();
repo.add("foo.txt");
repo.commit("First commit");

repo.add("bar.txt");
repo.commit("Second commit");

Result result = repo.log();
result.getMessage(); // see below
```

Съобщението при успех е следното:
```
message: "commit {hash}\nDate: {date}\n\n\t{message}"
# Повтаря се за всеки commit в текущия branch. Commit-ите се разделят с празен ред (т.е.
# два символа за нов ред). Последният commit се намира в началото на съобщението.
#
# {message} e съобщението на commit-а.
# {date} е датата, в която е направен commit-ът, във формат
# "<ден от седмицата> <месец> <ден от месец> <час>:<минути> <година>".
# Примери: "Thu Oct 25 11:13 2018", "Thu Nov 01 08:09 2018" или "Wed Nov 07 19:32 2018".
# {hash} е SHA1 hash на текста "{date}{message}".
```

Например от горния код фрагмент, `log()` трябва да върне следното съобщение.
```
commit b767221f31e87f225c7bd5175af9f7b91cbddc7f
Date: Thu Oct 25 11:13 2018

	Second commit

commit 3d504e8cfb88dd653ed9de81ca6709b257e69f62
Date: Thu Oct 25 11:13 2018

	First commit
```

#### String getBranch()

Връща името на текущия branch.

#### Result createBranch(String name)

Създава нов branch с име `name`. Branch-ът не е нищо повече от pointer към даден commit. 
Във всеки един момент хранилището има текущ branch. При опит за създаване на branch с име, което вече съществува, 
съобщението е `branch {name} already exists`. Новосъздаденият branch съдържа всички commit-и на текущия. 
Съобщението при успешна операция е `created branch {name}`.

#### Result checkoutBranch(String name)

Променя текущия branch. При опит за checkout към несъществуващ branch, съобщението е `branch {name} does not exist`. 
След като промените текущият branch, commit-ите, които правите след това, се append-ват само към текущия и другите branch-ове не знаят за тях. 
Съобщението при успешна операция е `switched to branch {name}`.

```java
Repository repo = new Repository();
repo.add("src/Main.java");
repo.commit("Add Main.java");

repo.createBranch("dev");
repo.checkoutBranch("dev");

repo.remove("src/Main.java");
repo.commit("Remove Main.java");
repo.getHead().getMessage(); // "Remove Main.java"

repo.checkoutBranch("master");
repo.getHead().getMessage(); // "Add Main.java"
```

#### Result checkoutCommit(String hash)

Позволява да се върнете към предишен commit от историята на текущия branch. След checkout на commit, 
всички по-нови commit-и след него стават недостъпни. При опит за checkout по hash, който не съществува съобщението е `commit {hash} does not exist`. 
А при успех - `HEAD is now at {hash}`.