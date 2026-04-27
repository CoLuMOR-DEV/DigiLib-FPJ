$env:DB_URL="jdbc:mysql://localhost:3306/digilib_fpj?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD=""
$env:PORT="8080"
$env:HARVARD_API_BASE_URL="https://api.lib.harvard.edu/v2/items.json"
$env:HARVARD_API_KEY=""
$env:RECEIPT_OUTPUT_DIR="receipts"
$env:MEMBER_DISCOUNT_PERCENTAGE="10"

mvn spring-boot:run
