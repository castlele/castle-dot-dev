import { CSSProperties, ReactNode } from "react";
import styles from "./Container.module.css"

export function Row(
    {
        children,
        justifyContent,
    }: {
        children: ReactNode;
        justifyContent: CSSProperties["justifyContent"];
    }
) {
    return (
         //{{display: 'flex', flexDirection: 'row', gap: '16px', alignItems: 'center'}}
        <div className={styles.row} style={{justifyContent}}>
            {children}
        </div>
    )
}

