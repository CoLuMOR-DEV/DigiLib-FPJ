$ErrorActionPreference = "Stop"

$paths = @(
    "$env:USERPROFILE\.m2\repository\org\apache\maven\shared\maven-filtering\3.3.1",
    "$env:USERPROFILE\.m2\repository\commons-io\commons-io\2.11.0",
    "$env:USERPROFILE\.m2\repository\org\apache\maven\plugins\maven-resources-plugin\3.3.1"
)

foreach ($path in $paths) {
    if (Test-Path $path) {
        Remove-Item -Path $path -Recurse -Force
    }
}

mvn -U clean
mvn -U spring-boot:run
