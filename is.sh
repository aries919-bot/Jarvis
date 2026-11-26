#!/bin/bash
ARIES_BASE_BRANCH="${ARIES_BASE_BRANCH:-kariyer-modulu}"
case "$1" in
  baslat) git switch -c "is/$2" ;;
  denetle)
    git add -A; git diff --cached "$ARIES_BASE_BRANCH" > .git/fark.diff
    [ -s .git/fark.diff ] || { echo "fark yok"; exit 1; }
    rm -f .git/onay
    python3 denetle.py | tee .git/denetim.txt
    [ "$(grep -v '^[[:space:]]*$' .git/denetim.txt | tail -n 1 | tr -d '*[:space:]')" = "ONAY" ] && touch .git/onay && echo ">> ONAY alindi" || echo ">> ONAY YOK"
    ;;
  bitir)
    bash "$0" denetle
    [ -f .git/onay ] || { echo "RED: .git/denetim.txt oku, duzelt, tekrar bitir"; exit 1; }
    rm .git/onay; b=$(git branch --show-current); git add -A; git commit -m "$b"
    git switch main && git merge --no-ff "$b" -m "birlestir: $b" ;;
  *) echo "kullanim: is.sh baslat <ad> | denetle | bitir" ;;
esac
